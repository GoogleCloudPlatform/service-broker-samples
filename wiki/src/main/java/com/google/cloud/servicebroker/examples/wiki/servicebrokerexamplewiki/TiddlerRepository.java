package com.google.cloud.servicebroker.examples.wiki.servicebrokerexamplewiki;

import org.springframework.stereotype.Repository;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

@Repository
public interface TiddlerRepository extends DatastoreRepository<Tiddler, String> {

}
