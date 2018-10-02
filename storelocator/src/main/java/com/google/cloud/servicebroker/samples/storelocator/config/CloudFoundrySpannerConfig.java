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

package com.google.cloud.servicebroker.samples.storelocator.config;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gcp.autoconfigure.spanner.GcpSpannerAutoConfiguration;
import org.springframework.cloud.gcp.autoconfigure.spanner.GcpSpannerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * Spanner Configuration for deploying on Cloud Foundry.
 */
@Configuration
@Conditional(CloudFoundryDeploymentCondition.class)
@EnableConfigurationProperties(GcpSpannerProperties.class)
public class CloudFoundrySpannerConfig {

  /**
   * Gets the google credentials from the "VCAP_SERVICES" environment variable.
   * The first google-spanner credential is assumed to be the correct credential.
   *
   * @param gcpSpannerProperties Spanner properties configuration
   * @return GoogleCredentials for Spanner
   * @throws IOException when GoogleCredentials creation fails due to invalid "VCAP_SERVICES" content
   */
  @Bean
  public GoogleCredentials spannerCredentials(GcpSpannerProperties gcpSpannerProperties)
      throws IOException {

    final String env = System.getenv("VCAP_SERVICES");
    final JSONObject credentials = new JSONObject(env)
        .getJSONArray("google-spanner")
        .getJSONObject(0)
        .getJSONObject("credentials");

    gcpSpannerProperties.setProjectId(credentials.getString("ProjectId"));
    gcpSpannerProperties.setInstanceId(credentials.getString("instance_id"));
    gcpSpannerProperties.setDatabase("storelocator");

    final InputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(
        credentials.getString("PrivateKeyData")));

    return GoogleCredentials.fromStream(stream);
  }

  /**
   * Supplies a CredentialsProvider to the {@link GcpSpannerAutoConfiguration}.
   *
   * This ensuyres the spannerCredentials bean is used.
   *
   * @param credentials the spannerCredentials bean
   * @return A provider for the spannerCredentials bean
   */
  @Bean
  public CredentialsProvider spannerCredentialsProvider(
      @Qualifier("spannerCredentials") GoogleCredentials credentials) {
    return () -> credentials;
  }

}
