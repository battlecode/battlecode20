#!/usr/bin/env python3

import logging
import multiprocessing
import time
import signal

from google.cloud import pubsub_v1


multiprocessing.log_to_stderr()
multiprocessing.get_logger().handlers[0].setFormatter(logging.Formatter(
    '%(asctime)s [%(threadName)-12.12s] [%(levelname)-5.5s]  %(message)s'))
logging.getLogger().addHandler(multiprocessing.get_logger().handlers[0])
logging.getLogger().setLevel(logging.INFO)

GCLOUD_PROJECT_ID        = 'battlecode18'
GCLOUD_SUBSCRIPTION_NAME = 'bc20-compile-sub'

ACK_DEADLINE = 30 # Value to which ack deadline is reset
SLEEP_TIME   = 10 # Interval between checks for new jobs and ack deadline

shutdown_requested = False # Whether the process should shut down due to SIGINT


def subscribe(worker):
    """Receives and spawns threads to handle jobs received in Pub/Sub"""

    client = pubsub_v1.SubscriberClient()
    subscription_path = client.subscription_path(GCLOUD_PROJECT_ID, GCLOUD_SUBSCRIPTION_NAME)
    logging.info('Listening for jobs')

    # Repeatedly check for new jobs until SIGINT received
    while not shutdown_requested:
        response = client.pull(subscription_path, max_messages=1)

        if not response.received_messages:
            logging.info('Job queue is empty')
            time.sleep(SLEEP_TIME)
            continue

        if len(response.received_messages) > 1:
            logging.warning('Received more than one job when only one expected')

        message = response.received_messages[0]

        process = multiprocessing.Process(target=worker, args=(message.message.data.decode(),))
        process.start()
        logging.info('Job {}: beginning'.format(message.message.data))

        while True:
            # If the process is still running, give it more time to finish
            # Reset ack deadline regularly to prevent PubSub from resending message
            if process.is_alive():
                client.modify_ack_deadline(
                    subscription_path,
                    [message.ack_id],
                    ack_deadline_seconds=ACK_DEADLINE)
                logging.debug('Reset ack deadline for {} for {}s'.format(
                    message.message.data, ACK_DEADLINE))

            # If the process is finished, acknowledge it
            else:
                client.acknowledge(subscription_path, [message.ack_id])
                logging.info('Job {}: ending and acknowledged'.format(message.message.data))
                break

            # Sleep the thread before checking again
            time.sleep(SLEEP_TIME)


def sigint_handler(signal, frame):
    global shutdown_requested
    shutdown_requested = True
    logging.warning('SIGINT received; shutting down')

signal.signal(signal.SIGINT, sigint_handler)
