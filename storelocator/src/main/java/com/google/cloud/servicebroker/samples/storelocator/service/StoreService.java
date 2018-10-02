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
import static java.util.Objects.requireNonNull;

import com.google.cloud.servicebroker.samples.storelocator.data.LocationBounds;
import com.google.cloud.servicebroker.samples.storelocator.data.Store;
import com.google.cloud.servicebroker.samples.storelocator.repository.StoreRepository;
import com.google.cloud.spanner.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gcp.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import org.springframework.cloud.gcp.data.spanner.core.admin.SpannerSchemaUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class StoreService {

  private static final Logger LOGGER
      = LoggerFactory.getLogger(StoreService.class);

  private final StoreRepository storeRepository;

  public StoreService(StoreRepository storeRepository) {
    this.storeRepository = requireNonNull(storeRepository, "storeRepository");
  }

  /**
   * Builds the {@link Statement} for performing a findWithinBounds query.
   *
   * @param bounds The bounds for the query.
   * @return A Spanner {@link Statement} for the query.
   */
  Statement buildStatement(LocationBounds bounds) {
    return Statement
        .newBuilder("SELECT * FROM " + TABLE_NAME
            + " WHERE latitude BETWEEN @lat1 AND @lat2 AND longitude BETWEEN @lng1 AND @lng2")
        .bind("lat1").to(bounds.getLat1())
        .bind("lat2").to(bounds.getLat2())
        .bind("lng1").to(bounds.getLng1())
        .bind("lng2").to(bounds.getLng2())
        .build();
  }

  public List<Store> findWithinBounds(LocationBounds bounds) {
    return storeRepository.getSpannerTemplate().query(Store.class, buildStatement(bounds));
  }

  /**
   * Initializes the Spanner database and table.
   *
   * @param spannerDatabaseAdminTemplate The Spanner database Template.
   * @param spannerSchemaUtils Spanner database utility object.
   */
  public void initializeTable(SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate,
      SpannerSchemaUtils spannerSchemaUtils) {
    if (!spannerDatabaseAdminTemplate.tableExists(Store.TABLE_NAME)) {
      LOGGER.info("Creating database:table " + spannerDatabaseAdminTemplate.getDatabase() + ":"
          + Store.TABLE_NAME);
      spannerDatabaseAdminTemplate.executeDdlStrings(
          Collections.singletonList(
              spannerSchemaUtils.getCreateTableDDLString(Store.class)),
          true);
    }
  }
}
