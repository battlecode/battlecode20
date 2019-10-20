#!/usr/bin/env python3

import subscription

import sys
import os
import logging
import subprocess
import threading

from google.cloud import storage

GCLOUD_BUCKET_ID         = 'bc20-submissions'


def monitor_command(command, cwd, timeout=0):
    """
    Executes a command-line instruction, with a specified timeout (or 0 for no timeout)
    Returns (exitcode, stdout, stderr) upon completion, or None if timeout
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
    return None

def worker(msg_data):
    """Performs a compilation job as specified in msg_data"""

    client = storage.Client()
    bucket = client.get_bucket(GCLOUD_BUCKET_ID)

    # Obtain compressed archive of the submission
    try:
        os.mkdir('/tmp/bc20-compile-'+msg_data)
        with open('/tmp/bc20-compile-'+msg_data+'/source.zip', 'wb') as file_obj:
            bucket.get_blob(msg_data+'/source.zip').download_to_file(file_obj)
    except:
        logging.error('Could not retrieve file from bucket')
        sys.exit(1)

    # TODO: unzip file, with timeout
    # TODO: invoke compilation, with timeout
    # TODO: send executable to bucket; report to database

if __name__ == '__main__':
    subscription.subscribe(worker)
