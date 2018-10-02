# Store Locator

Store Locator is a Spring Boot Application which displays store locations on a map.

# Setup
## Credentials
 * Get an API key for the Google Maps API
 * Get a Service Account JSON key for a Spanner instance
 * Ensure the Google Maps API and the Spanner APIs are enabled

## Infrastructure
 * Create a Spanner instance.

# Build
* Edit `application.properties` appropriately. A `.envrc.template` is also provided for direnv users.
* Cloud Foundry users will need to update `manifest.yml`
* `mvn package`

# Deploy
## Local
 * `java -jar target/store-locator-0.0.1-SNAPSHOT.jar`
 
## Cloud Foundry
* `cf create-service google-spanner sandbox storelocator-spanner`
* `cf service storelocator`
* `cf bind-service storelocator storelocator-spanner -c '{"role":"spanner.databaseAdmin"}'`
* `cf push -p target/store-locator-0.0.1-SNAPSHOT.jar`
 
# Technologies Showcased
* [Google Spanner](https://cloud.google.com/spanner/)
* [Google Maps Javascript API](https://developers.google.com/maps/documentation/javascript/tutorial)

# Open Source Client-Side Technologies Used
* [Bootstrap](https://getbootstrap.com/) - MIT License
* [jQuery](https://jquery.com/) - MIT License
* [Vue.js](https://vuejs.org/) - MIT License
