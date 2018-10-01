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
import com.google.api.services.pagespeedonline.model.PagespeedApiPagespeedResponseV4;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ScreenshotService {
  @Value("${google.api.key}")
  private String googleApiKey;

  private final Pagespeedonline client;

  public ScreenshotService() throws GeneralSecurityException, IOException {
    client = new Pagespeedonline.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory
         .getDefaultInstance(), null).build();
  }

  @Cacheable("screenshot-service")
  public byte[] getScreenshot(String url) {
    try {
      final Runpagespeed request = client.pagespeedapi().runpagespeed(url);
      request.setScreenshot(true);
      request.setRule(Collections.singletonList("MinifyHTML"));
      final PagespeedApiPagespeedResponseV4 response = request.execute();
      return response.getScreenshot().decodeData();
    } catch(IOException e) {
      e.printStackTrace();
      return new byte[]{};
    }
  }
}
