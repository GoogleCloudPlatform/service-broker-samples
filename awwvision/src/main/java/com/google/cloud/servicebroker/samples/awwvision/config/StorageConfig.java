/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.cloud.servicebroker.samples.awwvision.config;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gcp.autoconfigure.core.GcpProperties;
import org.springframework.cloud.gcp.autoconfigure.storage.GcpStorageAutoConfiguration;
import org.springframework.cloud.gcp.autoconfigure.storage.GcpStorageProperties;
import org.springframework.cloud.gcp.core.DefaultCredentialsProvider;
import org.springframework.cloud.gcp.core.GcpProjectIdProvider;
import org.springframework.cloud.gcp.core.UsageTrackingHeaderProvider;
import org.springframework.cloud.gcp.storage.GoogleStorageProtocolResolver;
import org.springframework.cloud.gcp.storage.GoogleStorageProtocolResolverSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.io.IOException;

/**
 * Sets up connections to client libraries and other injectable beans.
 */
@Configuration
@EnableConfigurationProperties({GcpProperties.class, GcpStorageProperties.class})
@Import(GoogleStorageProtocolResolver.class)
public class StorageConfig {

  /**
   * Gotta get that storage.
   * 
   * @param gcpStorageProperties props man.
   * @param projectIdProvider more props man.
   */
  @Bean
  public Storage storage(
      CredentialsProvider credentialsProvider,
      GcpStorageProperties gcpStorageProperties,
      GcpProjectIdProvider projectIdProvider) throws IOException {

    System.out.println("WOWOWOWOWOWOWOWO");

    return GcpStorageAutoConfiguration.storage(credentialsProvider,
        gcpStorageProperties, projectIdProvider);
  }
}
