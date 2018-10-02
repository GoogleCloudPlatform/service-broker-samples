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

import com.google.cloud.servicebroker.samples.storelocator.data.LocationBounds;
import com.google.cloud.servicebroker.samples.storelocator.data.Store;
import com.google.cloud.servicebroker.samples.storelocator.service.StoreService;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Provides a REST API for querying {@link Store} objects on a geographic coordinate system.
 */
@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {

  private final StoreService storeService;

  public StoreController(StoreService storeService) {
    this.storeService = requireNonNull(storeService, "storeService");
  }

  /**
   * Retrieves {@link Store} objects given a {@link LocationBounds}.
   *
   * @param bounds The bounding box to search for locations.
   * @return a {@link List} of locations inside the given bounding box.
   */
  @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public List<Store> locations(LocationBounds bounds) {
    return storeService.findWithinBounds(bounds);
  }
}
