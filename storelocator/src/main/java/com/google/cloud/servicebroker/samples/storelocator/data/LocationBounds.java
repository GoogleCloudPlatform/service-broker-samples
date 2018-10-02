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
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Two-dimensional bounding box for finding locations on a geographic coordinate system.
 */
public class LocationBounds {

  private final double lat1;
  private final double lng1;
  private final double lat2;
  private final double lng2;

  /**
   * Creates a LocationBox given two coordinate pairs.
   *
   * <p>The smaller latitude and longitude values will always be assigned to lat1 and lng1
   * respectively.
   *
   * @param lat1 latitude of first coordinate pair.
   * @param lng1 longitude of first coordinate pair.
   * @param lat2 latitude of second coordinate pair.
   * @param lng2 longitude of second coordinate pair.
   */
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
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }
}

