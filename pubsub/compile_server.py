#!/usr/bin/env python3

import subscription

import sys
import os
import shutil
import logging
import subprocess
import threading

from google.cloud import storage

GCLOUD_BUCKET_ID = 'bc20-submissions'
UNZIP_TIMEOUT   = 10
COMPILE_TIMEOUT = 90


def db_report_result(submissionid, result):
    """Sends the result of the run to the database"""
    try:
        # TODO report to database
        pass
    except:
        logging.critical('Could not report to database')

def report_error(submissionid, reason):
    """Reports a server-side error to the database and terminates with failure"""
    db_report_result(submissionid, 'error')
    logging.error(reason)
    sys.exit(1)

def monitor_command(command, cwd, timeout=0):
    """
    Executes a command-line instruction, with a specified timeout (or 0 for no timeout)
    Returns (exitcode, stdout, stderr) upon completion, or (-1, '', '') if timeout
    """
    subproc = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, cwd=cwd)
    if timeout > 0:
        to = threading.Timer(timeout, subproc.kill)
        try:
            to.start()
            proc_stdout, proc_stderr = subproc.communicate()
            return (subproc.returncode, proc_stdout, proc_stderr)
        finally:
            to.cancel()
    else:
        proc_stdout, proc_stderr = subproc.communicate()
        return (subproc.returncode, proc_stdout, proc_stderr)
    return (-1, '', '')

def compile_worker(submissionid):
    """Performs a compilation job as specified in submissionid"""

    client = storage.Client()
    bucket = client.get_bucket(GCLOUD_BUCKET_ID)

    # Filesystem structure:
    # /tmp/bc20-compile-{submissionid}/
    #     `-- source.zip
    #     `-- source/
    #     |      `-- all contents of source.zip
    #     `-- player.jar
    rootdir   = os.path.join('/', 'tmp', 'bc20-compile-'+submissionid)
    sourcedir = os.path.join(rootdir, 'source')

    # Obtain compressed archive of the submission
    try:
        os.mkdir(rootdir)
        os.mkdir(sourcedir)
        with open(os.path.join(rootdir, 'source.zip'), 'wb') as file_obj:
            bucket.get_blob(os.path.join(submissionid, 'source.zip')).download_to_file(file_obj)
    except:
        report_error(submissionid, 'Could not retrieve source file from bucket')

    # Decompress submission archive
    result = monitor_command(
        ['unzip', 'source.zip', '-d', sourcedir],
        cwd=rootdir,
        timeout=UNZIP_TIMEOUT)
    if result[0] != 0:
        report_error(submissionid, 'Could not decompress source file')

    # TODO: Invoke compilation to produce executable jar
    result = monitor_command(
        ['cp', os.path.join('..', 'source.zip'), os.path.join('..', 'player.jar')],
        cwd=sourcedir,
        timeout=COMPILE_TIMEOUT)

    if result[0] == 0:
        # The compilation succeeded; send the jar to the bucket for storage
        try:
            with open(os.path.join(rootdir, 'player.jar'), 'rb') as file_obj:
                bucket.blob(os.path.join(submissionid, 'player.jar')).upload_from_file(file_obj)
        except:
            report_error(submissionid, 'Could not send executable to bucket')
        db_report_result(submissionid, 'success')
    else:
        # The compilation failed; report this to database
        db_report_result(submissionid, 'failed')

    # Clean up working directory
    try:
        shutil.rmtree(rootdir)
    except:
        logging.warning('Could not clean up compilation directory')

if __name__ == '__main__':
    subscription.subscribe(compile_worker)
