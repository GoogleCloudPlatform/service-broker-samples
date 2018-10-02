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

import static com.google.cloud.servicebroker.samples.storelocator.data.Store.TABLE_NAME;

import com.google.cloud.servicebroker.samples.storelocator.data.LocationBounds;
import com.google.cloud.servicebroker.samples.storelocator.data.Store;
import com.google.cloud.servicebroker.samples.storelocator.repository.StoreRepository;
import com.google.cloud.spanner.Statement;

import org.springframework.stereotype.Component;

@Component
public class StoreService extends AbstractSpannerLocationService<Store> {

  public StoreService(StoreRepository storeRepository) {
    super(storeRepository, TABLE_NAME, Store.class);
  }

  @Override
  protected Statement buildStatement(LocationBounds bounds) {
    return Statement
        .newBuilder("SELECT * FROM " + TABLE_NAME
            + " WHERE latitude BETWEEN @lat1 AND @lat2 AND longitude BETWEEN @lng1 AND @lng2")
        .bind("lat1").to(bounds.getLat1())
        .bind("lat2").to(bounds.getLat2())
        .bind("lng1").to(bounds.getLng1())
        .bind("lng2").to(bounds.getLng2())
        .build();
  }
}
