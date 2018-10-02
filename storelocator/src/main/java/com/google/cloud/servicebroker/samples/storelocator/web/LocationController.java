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

package com.google.cloud.servicebroker.samples.storelocator.web;

import static java.util.Objects.requireNonNull;

import com.google.cloud.servicebroker.samples.storelocator.data.Locatable;
import com.google.cloud.servicebroker.samples.storelocator.data.LocationBounds;
import com.google.cloud.servicebroker.samples.storelocator.service.LocationService;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Provides a REST API for querying {@link Locatable} objects on a geographic coordinate system.
 */
@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

  private final Collection<LocationService<?>> repositories;

  @Autowired
  public LocationController(Collection<LocationService<?>> repositories) {
    this.repositories = requireNonNull(repositories, "repositories");

  }

  /**
   * Retrieves {@link Locatable} objects given a {@link LocationBounds}.
   *
   * The returned result is the union of the results of each {@link LocationService}.
   *
   * @param bounds The bounding box to search for locations.
   * @return a {@link List} of locations inside the given bounding box.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<Locatable> locations(LocationBounds bounds) {
    return repositories.stream()
        .map(repo -> repo.findWithinBounds(bounds))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }
}
