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

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * A physical location given by coordinates.
 */
public interface Locatable {

  double getLatitude();

  double getLongitude();

  /**
   * Returns a location type String, meant for distinguishing between implementations.
   *
   * <p>Locatable objects are expected to be serialized to JSON. Including a type field will help
   * clients distinguish how to render different JSON objects.
   *
   * <p>By default this is the classname of the implementation.
   *
   * @return a type String
   */
  @JsonGetter
  default String locationType() {
    return getClass().getName();
  }
}
