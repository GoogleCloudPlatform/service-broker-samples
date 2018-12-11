# Store Locator

Store Locator is a Spring Boot Application which displays store locations on a map.

# Build
* Get an API key for the Google Maps API
* Edit `application.properties` appropriately. A `.envrc.template` is also provided for direnv users.
* Update `manifest.yml` to include the Google Maps API key
* `mvn package`

# Deploy
* `cf create-service google-spanner sandbox storelocator-spanner`
* `cf service storelocator`
* `cf push --no-start -p target/store-locator-0.0.1-SNAPSHOT.jar storelocator`
* `cf bind-service storelocator storelocator-spanner -c '{"role":"spanner.databaseAdmin"}'`
* `cf start storelocator`
 
# Technologies Showcased
* [Google Spanner](https://cloud.google.com/spanner/)
* [Google Maps JavaScript API](https://developers.google.com/maps/documentation/javascript/tutorial)

# Open Source Client-Side Technologies Used
* [Bootstrap](https://getbootstrap.com/) - MIT License
* [jQuery](https://jquery.com/) - MIT License
* [Vue.js](https://vuejs.org/) - MIT License
