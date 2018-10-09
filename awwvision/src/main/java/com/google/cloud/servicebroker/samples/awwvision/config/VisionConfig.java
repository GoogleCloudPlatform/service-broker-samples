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

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.servicebroker.samples.awwvision.MockableImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageAnnotatorSettings;

import org.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * Sets up connections to client libraries and other injectable beans.
 */
@Configuration
public class VisionConfig {

  @Bean
  GoogleCredentials visionCredential() throws IOException {
    String env = System.getenv("VCAP_SERVICES");

    String privateKeyData =
        new JSONObject(env)
            .getJSONArray("google-ml-apis")
            .getJSONObject(0)
            .getJSONObject("credentials")
            .getString("PrivateKeyData");

    InputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(privateKeyData));
    return GoogleCredentials.fromStream(stream);
  }

  @Bean
  MockableImageAnnotatorClient imageAnnotatorClient(GoogleCredentials visionCredential)
      throws IOException {

    return new MockableImageAnnotatorClient(
        ImageAnnotatorClient.create(ImageAnnotatorSettings.newBuilder()
            .setCredentialsProvider(() -> visionCredential)
            .build()));
  }
}
