/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.cloud.servicebroker.samples.storelocator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for the store locator application.
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "store-locator")
public class StoreLocatorProperties {

  private GoogleMapProperties googleMap;

  private boolean testDataEnabled;

  private int databaseInitTimeoutSeconds = 120;

  public GoogleMapProperties getGoogleMap() {
    return googleMap;
  }

  public void setGoogleMap(GoogleMapProperties googleMap) {
    this.googleMap = googleMap;
  }

  public boolean isTestDataEnabled() {
    return testDataEnabled;
  }

  public void setTestDataEnabled(boolean testDataEnabled) {
    this.testDataEnabled = testDataEnabled;
  }

  public int getDatabaseInitTimeoutSeconds() {
    return databaseInitTimeoutSeconds;
  }

  public void setDatabaseInitTimeoutSeconds(int databaseInitTimeoutSeconds) {
    this.databaseInitTimeoutSeconds = databaseInitTimeoutSeconds;
  }
}
