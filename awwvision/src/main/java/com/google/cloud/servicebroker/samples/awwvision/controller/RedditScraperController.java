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

package com.google.cloud.servicebroker.samples.awwvision.controller;

import com.google.cloud.servicebroker.samples.awwvision.data.RedditResponse;
import com.google.cloud.servicebroker.samples.awwvision.data.RedditResponse.Listing;
import com.google.cloud.servicebroker.samples.awwvision.service.CuteImageService;
import com.google.cloud.servicebroker.samples.awwvision.service.ImageLabelingService;
import com.google.cloud.storage.Blob;
import com.google.common.collect.ImmutableMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Provides a request mapping for scraping images from reddit, labeling them with the Vision API,
 * and storing them in Cloud Storage.
 */
@Controller
public class RedditScraperController {

  private static final String REDDIT_URL = "https://www.reddit.com/r/aww/hot.json";

  @Autowired
  private ImageLabelingService imageLabelingService;

  @Autowired
  private CuteImageService cuteImageService;

  private final Log logger = LogFactory.getLog(getClass());

  @Value("${reddit-user-agent}")
  private String redditUserAgent;

  /**
   * Scrapes https://reddit.com/r/aww for cute pictures are stores them in a Storage bucket.
   *
   * @param restTemplate The RestTemplate.
   * @return The view to render.
   */
  @RequestMapping("/reddit")
  public String getRedditUrls(RestTemplate restTemplate) {
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.USER_AGENT, redditUserAgent);
    RedditResponse response = restTemplate
        .exchange(REDDIT_URL, HttpMethod.GET, new HttpEntity<String>(headers), RedditResponse.class)
        .getBody();

    storeAndLabel(response);

    return "reddit";
  }

  void storeAndLabel(RedditResponse response) {
    for (Listing listing : response.data.children) {
      storeAndLabel(listing); // functional support?
    }
  }

  private void storeAndLabel(final Listing listing) {
    Objects.requireNonNull(listing, "listing must not be null");
    if (listing.data.preview == null) { // is this a bug that should be listing.data.url?
      return;
    }

    final String dataUrl = listing.data.url;
    try {
      // Only label and upload the image if it does not already exist in storage.
      final Blob existing = cuteImageService.get(dataUrl);
      if (existing != null) {
        return;
      }

      final URL url = new URL(dataUrl);
      final byte[] raw = download(url);
      final String label = imageLabelingService.labelImage(raw);
      if (label != null) {
        cuteImageService.uploadJpeg(dataUrl, url, ImmutableMap.of("label", label));
      }
    } catch (IOException ex) {
      logger.error("Issue with labeling or uploading image " + dataUrl, ex);
    }
  }

  byte[] download(URL url) throws IOException {
    return IOUtils.toByteArray(url.openStream());
  }
}
