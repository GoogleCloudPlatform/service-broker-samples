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

package com.google.cloud.servicebroker.samples.storelocator.service;

import com.google.cloud.servicebroker.samples.storelocator.data.Locatable;
import com.google.cloud.servicebroker.samples.storelocator.data.LocationBounds;
import java.util.Collection;

/**
 * Indicates a class is a service for querying {@link Locatable}s.
 */
public interface LocationService<S extends Locatable> {

  /**
   * Returns zero or more {@link Locatable}s given {@link LocationBounds}.
   *
   * @param bounds boundary to filter locations
   * @return A non-null Collection of Locations
   */
  Collection<S> findWithinBounds(LocationBounds bounds);
}
