package com.google.cloud.servicebroker.samples.storelocator.config;

/**
 * ConfigurationProperties for rendering the Google Map.
 */
public class GoogleMapProperties {

  private double lat;
  private double lng;

  // See https://developers.google.com/maps/documentation/javascript/tutorial#zoom-levels
  private int zoomLevel;
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

  public int getZoomLevel() {
    return zoomLevel;
  }

  public void setZoomLevel(int zoomLevel) {
    this.zoomLevel = zoomLevel;
  }
}
