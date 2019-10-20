# Configurations in Google Cloud

In GCloud > PubSub:
- Create new topic. Call it bc20-compile
- Create new subscriber. Call it bc20-compile-sub
  All compile servers will share this subscriber

In GCloud > IAM > Service accounts:
- Create new service account
- Add roles: PubSub publisher, PubSub subscriber
- Download JSON key; call it ~/blah/key.json (or something more reasonable)

In GCloud > Storage:
- Create new bucket. Call it bc20-submissions
- Grant permissions to the service account: Storage Legacy Bucket Reader/Writer

# Local configurations

It's probably better to do this in a virtualenv.

Requirements:
```
pip3 install --upgrade google-cloud-pubsub google-cloud-storage
```

Open multiple terminals. In each of them, run all of this.
```
export GOOGLE_APPLICATION_CREDENTIALS=~/blah/key.json
export PROJECT=`gcloud config get-value project`
```

And then run one command per terminal.
```
python3 pub.py $PROJECT bc20-compile
python3 compile_server.py
```
