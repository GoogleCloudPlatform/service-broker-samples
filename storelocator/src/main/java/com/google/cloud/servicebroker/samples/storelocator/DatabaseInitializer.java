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

import com.google.cloud.servicebroker.samples.storelocator.service.StoreService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gcp.data.spanner.core.admin.SpannerDatabaseAdminTemplate;
import org.springframework.cloud.gcp.data.spanner.core.admin.SpannerSchemaUtils;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * CommandLineRunner which creates the backing database and tables on startup.
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);

  private final SpannerSchemaUtils spannerSchemaUtils;
  private final SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate;
  private final StoreService storeService;

  @Autowired
  DatabaseInitializer(
      SpannerSchemaUtils spannerSchemaUtils,
      SpannerDatabaseAdminTemplate spannerDatabaseAdminTemplate,
      StoreService storeService) {
    this.spannerSchemaUtils = requireNonNull(spannerSchemaUtils, "spannerSchemaUtils");
    this.spannerDatabaseAdminTemplate = requireNonNull(spannerDatabaseAdminTemplate,
        "spannerDatabaseAdminTemplate");
    this.storeService = requireNonNull(storeService, "storeService");
  }

  @Override
  public void run(String... args) {
    LOGGER.info("Checking if Spanner tables exist...");

    // In rare cases, Spanner operations will always fail with retryable error codes.
    // This can cause the Spring application to hang indefinitely.
    // An ExecutorService here gives more assurance that the app will gracefully fail on error.
    final ExecutorService executor = Executors.newCachedThreadPool();
    final Future future = executor.submit(
        () -> storeService.initializeTable(spannerDatabaseAdminTemplate, spannerSchemaUtils));
    try {
      future.get(15, TimeUnit.SECONDS);
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
}
