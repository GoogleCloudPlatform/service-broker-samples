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

package com.google.cloud.servicebroker.samples.storelocator;

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

  private Map map;

  private GoogleResults googleResults;

  public Map getMap() {
    return map;
  }

  public void setMap(Map map) {
    this.map = map;
  }

  public GoogleResults getGoogleResults() {
    return googleResults;
  }

  public void setGoogleResults(
      GoogleResults googleResults) {
    this.googleResults = googleResults;
  }

  public static class Map {

    private double lat;
    private double lng;
    private int zoom;
    private String key;

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public double getLat() {
      return lat;
    }

    public void setLat(double lat) {
      this.lat = lat;
    }

    public double getLng() {
      return lng;
    }

    public void setLng(double lng) {
      this.lng = lng;
    }

    public int getZoom() {
      return zoom;
    }

    public void setZoom(int zoom) {
      this.zoom = zoom;
    }
  }

  public static class GoogleResults {

    private boolean enable;
    private String searchKeyword;

    public boolean isEnable() {
      return enable;
    }

    public void setEnable(boolean enable) {
      this.enable = enable;
    }

    public String getSearchKeyword() {
      return searchKeyword;
    }

    public void setSearchKeyword(String keyword) {
      this.searchKeyword = keyword;
    }
  }

}
