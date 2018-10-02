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

import static java.util.Objects.requireNonNull;

import com.google.cloud.servicebroker.samples.storelocator.data.Locatable;
import com.google.cloud.servicebroker.samples.storelocator.data.LocationBounds;
import com.google.cloud.servicebroker.samples.storelocator.data.Store;
import com.google.cloud.servicebroker.samples.storelocator.repository.StoreRepository;
import com.google.cloud.spanner.Key;
import com.google.cloud.spanner.Statement;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gcp.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import org.springframework.cloud.gcp.data.spanner.core.admin.SpannerSchemaUtils;
import org.springframework.cloud.gcp.data.spanner.repository.SpannerRepository;

/**
 * Abstract service class for querying a Spanner Database.
 *
 * @param <S> The location class
 */
public abstract class AbstractSpannerLocationService<S extends Locatable> implements LocationService<S> {

  private static final Logger LOGGER = LoggerFactory.getLogger(StoreRepository.class);

  private final SpannerRepository<S, Key> locationRepository;
  private final String tableName;
  private final Class<S> type;

  AbstractSpannerLocationService(SpannerRepository<S, Key> locationRepository, String tableName, Class<S> type) {
    this.locationRepository = requireNonNull(locationRepository, "locationRepository");
    this.tableName = requireNonNull(tableName, "tableName");
    this.type = requireNonNull(type, "type");
  }

  /**
   * Builds the {@link Statement} for performing a {@link LocationService#findWithinBounds} query.
   *
   * @param bounds The bounds for the query.
   * @return A Spanner {@link Statement} for the query.
   */
  protected abstract Statement buildStatement(LocationBounds bounds);

  @Override
  public List<S> findWithinBounds(LocationBounds bounds) {
    return locationRepository.getSpannerTemplate().query(type, buildStatement(bounds));
  }

  /**
   * Initializes the Spanner database and table.
   *
   * @param spannerDatabaseAdminTemplate The Spanner database Template.
   * @param spannerSchemaUtils Spanner database utility object.
   */
  public void initializeTable(SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate,
      SpannerSchemaUtils spannerSchemaUtils) {
    if (!spannerDatabaseAdminTemplate.tableExists(tableName)) {
      LOGGER.info("Creating database:table " + spannerDatabaseAdminTemplate.getDatabase() + ":"
          + tableName);
      spannerDatabaseAdminTemplate.executeDdlStrings(
          Collections.singletonList(
              spannerSchemaUtils.getCreateTableDDLString(Store.class)),
          true);
    }
  }

}
