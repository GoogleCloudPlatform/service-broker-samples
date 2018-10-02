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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Two-dimensional bounding box for finding locations on a geographic coordinate system.
 */
public class LocationBounds {

  private final double lat1, lng1, lat2, lng2;

  @JsonCreator
  public LocationBounds(
      @JsonProperty("lat1") double lat1,
      @JsonProperty("lng1") double lng1,
      @JsonProperty("lat2") double lat2,
      @JsonProperty("lng2") double lng2) {
    this.lat1 = Math.min(lat1, lat2);
    this.lng1 = Math.min(lng1, lng2);
    this.lat2 = Math.max(lat1, lat2);
    this.lng2 = Math.max(lng1, lng2);
  }

  public double getLat1() {
    return lat1;
  }

  public double getLng1() {
    return lng1;
  }

  public double getLat2() {
    return lat2;
  }

  public double getLng2() {
    return lng2;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    LocationBounds that = (LocationBounds) o;

    return new EqualsBuilder()
        .append(lat1, that.lat1)
        .append(lng1, that.lng1)
        .append(lat2, that.lat2)
        .append(lng2, that.lng2)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(lat1)
        .append(lng1)
        .append(lat2)
        .append(lng2)
        .toHashCode();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("lat1", lat1)
        .add("lng1", lng1)
        .add("lat2", lat2)
        .add("lng2", lng2)
        .toString();
  }
}

