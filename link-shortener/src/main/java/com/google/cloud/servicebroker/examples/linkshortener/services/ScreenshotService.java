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
package com.google.cloud.servicebroker.examples.linkshortener.services;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.pagespeedonline.Pagespeedonline;
import com.google.api.services.pagespeedonline.Pagespeedonline.Pagespeedapi.Runpagespeed;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * ScreenshotServices uses Google's Pagespeed API to get a JPEG of a page.
 */
@Service
public class ScreenshotService {

  private static final Logger LOG = LoggerFactory.getLogger(ScreenshotService.class);

  private final String googleApiKey;
  private final Pagespeedonline client;

  public ScreenshotService(@Value("${google.api.key}") String googleApiKey)
      throws GeneralSecurityException, IOException {
    Objects.requireNonNull(googleApiKey);
    this.googleApiKey = googleApiKey;

    client = new Pagespeedonline.Builder(
        GoogleNetHttpTransport.newTrustedTransport(),
        JacksonFactory.getDefaultInstance(),
        null)
        .build();
  }

  @Cacheable("screenshot-service")
  public byte[] getScreenshot(String url) {
    try {
      final Runpagespeed request = client.pagespeedapi().runpagespeed(url);
      request.setScreenshot(true);
      request.setRule(Collections.singletonList("MinifyHTML"));

      return request.execute().getScreenshot().decodeData();

    } catch (IOException e) {
      LOG.error("Couldn't get screenshot", e);
      return new byte[]{};
    }
  }
}
