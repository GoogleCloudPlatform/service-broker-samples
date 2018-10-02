# Wiki Demo

This example Spring application demonstrates using GCP Datastore as a backing-store for TiddlyWiki.
TiddlyWiki is "a non-linear personal web notebook that anyone can use and keep forever, independently of any corporation."

TiddlyWiki is a wiki organized into "cards", each with their own title and content.
The wiki can be exported as a single HTML file with all resources embedded for long-term storage.
This makes it great for communal knowledgebases that you can snapshot and share.

## Deploying

You must bind a Cloud Datastore instance to the app using the GCP Service Broker.


You can setup the environment using:

```bash
$ cf create-service google-datastore default tiddlywiki-storage
$ cf bind-service wiki tiddlywiki-storage
```


You can build and deploy the app using:

```bash
$ mvn package -DskipTests=true
$ cf push -p target/service-broker-example-wiki-0.0.1-SNAPSHOT.jar
```

## Technologies Showcased

* Google Cloud Datastore


## Open Source Client-Side Technologies Used

This application uses the following OSS client-side technologies:

* TiddlyWiki - BSD License
