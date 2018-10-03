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

package com.google.cloud.servicebroker.samples.storelocator;

import static java.util.Objects.requireNonNull;

import com.google.cloud.servicebroker.samples.storelocator.config.StoreLocatorProperties;
import com.google.cloud.servicebroker.samples.storelocator.data.Store;
import com.google.cloud.servicebroker.samples.storelocator.repository.StoreRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cloud.gcp.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import org.springframework.cloud.gcp.data.spanner.core.admin.SpannerSchemaUtils;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

/**
 * CommandLineRunner which creates the backing database and tables on startup.
 */
@Component
public class DatabaseInitializer implements ApplicationRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);

  private final SpannerSchemaUtils spannerSchemaUtils;
  private final SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;
  private final StoreRepository storeRepository;
  private final StoreLocatorProperties storeLocatorProperties;

  DatabaseInitializer(
      SpannerSchemaUtils spannerSchemaUtils,
      SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate,
      StoreRepository storeRepository,
      StoreLocatorProperties storeLocatorProperties) {
    this.spannerSchemaUtils = requireNonNull(spannerSchemaUtils, "spannerSchemaUtils");
    this.spannerDatabaseAdminTemplate = requireNonNull(spannerDatabaseAdminTemplate,
        "spannerDatabaseAdminTemplate");
    this.storeRepository = requireNonNull(storeRepository, "storeRepository");
    this.storeLocatorProperties = requireNonNull(storeLocatorProperties,
        "storeLocatorProperties");
  }

  @Override
  public void run(ApplicationArguments arguments) {
    initializeDatabaseWithTimeout();
  }

  private void initializeDatabaseWithTimeout() {
    LOGGER.info("Checking if Spanner tables exist...");

    // In rare cases, Spanner operations will always fail with retryable error codes.
    // This can cause the Spring application to hang indefinitely.
    // An ExecutorService here gives more assurance that the app will gracefully fail on error.
    final ExecutorService executor = Executors.newCachedThreadPool();
    final Future future = executor.submit(() -> {
      try {
        initializeDatabase();
      } catch (MalformedURLException ex) {
        throw new RuntimeException("Test data included a malformed URL", ex);
      }
    });

    try {
      future.get(storeLocatorProperties.getDatabaseInitTimeoutSeconds(), TimeUnit.SECONDS);
    } catch (TimeoutException ex) {
      LOGGER.error("Timeout initializing Spanner tables. Assuming they are unreachable.");
      LOGGER.error("Increase log level to check if retryable errors are incurring indefinitely.");
      throw new RuntimeException("Exception while initializing Spanner tables", ex);
    } catch (InterruptedException ignored) {
      // Ignore. Assume the application is already shutting down.
    } catch (ExecutionException ex) {
      throw new RuntimeException("Exception while initializing Spanner tables", ex);
    } finally {
      future.cancel(true);
    }

    LOGGER.info("Finished, Spanner tables exist.");
  }

  private void initializeDatabase() throws MalformedURLException {
    if (!spannerDatabaseAdminTemplate.tableExists(Store.TABLE_NAME)) {
      LOGGER.info("Creating database:table " + spannerDatabaseAdminTemplate.getDatabase() + ":"
          + Store.TABLE_NAME);
      spannerDatabaseAdminTemplate.executeDdlStrings(
          Collections.singletonList(
              spannerSchemaUtils.getCreateTableDDLString(Store.class)),
          true);

      if (storeLocatorProperties.isTestDataEnabled()) {
        addTestData();
      }
    }
  }

  private void addTestData() throws MalformedURLException {
    LOGGER.info("Adding test data");
    Stream.of(
        new Store(47.6419389,-122.3479717,"Matt's cool shop","123 cool street drive", new URL("https://www.google.com"),"6:00 AM","5:30 PM","(555) - 555 - 5555)"),
        new Store(47.6519389,-122.5479717,"Super Sweet Cafe","1826 SW green ave", new URL("https://www.google.com"),"8:00 AM","8:00 PM","(555) - 555 - 5555"),
        new Store(47.647011,-122.3450999,"A Google building","192 West Google Drive", new URL("https://www.google.com"),"8:00 AM","8:00 PM","(555) - 555 - 5555")
    ).forEach(storeRepository::save);
  }
}
