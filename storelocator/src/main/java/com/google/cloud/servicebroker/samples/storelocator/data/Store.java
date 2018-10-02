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

package com.google.cloud.servicebroker.samples.storelocator.data;

import static com.google.cloud.servicebroker.samples.storelocator.data.Store.TABLE_NAME;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import java.net.URL;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Column;
import org.springframework.cloud.gcp.data.spanner.core.mapping.PrimaryKey;
import org.springframework.cloud.gcp.data.spanner.core.mapping.Table;

/**
 * An entity object representing a simplified store location.
 * <p>
 * This object was heavily inspired by the Google Maps API Places resource.
 * See https://developers.google.com/maps/documentation/javascript/reference/places-service#PlaceResult
 * <p>
 * Only the latitude and longitude are used as the primary key.
 * This makes it trivial to create search queries using a coordinate bounding box.
 * <p>
 * For real use, this entity should be updated or replaced with a better defined and stronger typed model.
 * Many of the fields have been simplified for demonstration purposes.
 * For example, the openingTime and closingTime fields are a gross approximation of the model
 * required to accurately portray a business location's hour of operations.
 * Many of the fields are simply typed to String, such as phoneNumber. This is again for the simplicity of demonstration.
 *
 * @see LocationBounds
 */
@Table(name = TABLE_NAME)
public class Store implements Locatable {

  public static final String TABLE_NAME = "stores";

  @Column(name = "latitude")
  @PrimaryKey
  private final double latitude;

  @Column(name = "longitude")
  @PrimaryKey(keyOrder = 2)
  private final double longitude;

  @Column(name = "name")
  private final String name;

  @Column(name = "address")
  private final String address;

  @Column(name = "website")
  private final URL website;

  @Column(name = "openingTime")
  private final String openingTime;

  @Column(name = "closingTime")
  private final String closingTime;

  @Column(name = "phoneNumber")
  private final String phoneNumber;

  @JsonCreator
  public Store(
      @JsonProperty("latitude") double latitude,
      @JsonProperty("longitude") double longitude,
      @JsonProperty("name") String name,
      @JsonProperty("address") String address,
      @JsonProperty("website") URL website,
      @JsonProperty("openingTime") String openingTime,
      @JsonProperty("closingTime") String closingTime,
      @JsonProperty("phoneNumber") String phoneNumber) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.name = name;
    this.address = address;
    this.website = website;
    this.openingTime = openingTime;
    this.closingTime = closingTime;
    this.phoneNumber = phoneNumber;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public Optional<String> getName() {
    return Optional.ofNullable(name);
  }

  public Optional<String> getAddress() {
    return Optional.ofNullable(address);
  }

  public Optional<URL> getWebsite() {
    return Optional.ofNullable(website);
  }

  public Optional<String> getOpeningTime() {
    return Optional.ofNullable(openingTime);
  }

  public Optional<String> getClosingTime() {
    return Optional.ofNullable(closingTime);
  }

  public Optional<String> getPhoneNumber() {
    return Optional.ofNullable(phoneNumber);
  }

  @Override
  public String locationType() {
    return "store";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Store store = (Store) o;

    return new EqualsBuilder()
        .append(latitude, store.latitude)
        .append(longitude, store.longitude)
        .append(name, store.name)
        .append(address, store.address)
        .append(website, store.website)
        .append(openingTime, store.openingTime)
        .append(closingTime, store.closingTime)
        .append(phoneNumber, store.phoneNumber)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(latitude)
        .append(longitude)
        .append(name)
        .append(address)
        .append(website)
        .append(openingTime)
        .append(closingTime)
        .append(phoneNumber)
        .toHashCode();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("latitude", latitude)
        .add("longitude", longitude)
        .add("name", name)
        .add("address", address)
        .add("website", website)
        .add("openingTime", openingTime)
        .add("closingTime", closingTime)
        .add("phoneNumber", phoneNumber)
        .toString();
  }
}
