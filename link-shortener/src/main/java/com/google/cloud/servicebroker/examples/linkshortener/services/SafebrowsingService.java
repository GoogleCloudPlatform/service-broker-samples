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
import com.google.api.services.safebrowsing.Safebrowsing;
import com.google.api.services.safebrowsing.model.ClientInfo;
import com.google.api.services.safebrowsing.model.FindThreatMatchesRequest;
import com.google.api.services.safebrowsing.model.FindThreatMatchesResponse;
import com.google.api.services.safebrowsing.model.ThreatEntry;
import com.google.api.services.safebrowsing.model.ThreatInfo;
import com.google.api.services.safebrowsing.model.ThreatMatch;
import com.google.cloud.servicebroker.examples.linkshortener.enums.ThreatStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * SafebrowsingService provides access to the Google Safebrowsing API which is what protects Chrome
 * users from going to malicious sites.
 */
@Service
public class SafebrowsingService {

  private static final Logger LOG = LoggerFactory.getLogger(SafebrowsingService.class);

  private final String googleApiKey;
  private final Safebrowsing client;

  SafebrowsingService(@Value("${google.api.key}") String googleApiKey)
      throws GeneralSecurityException, IOException {
    Objects.requireNonNull(googleApiKey);

    this.googleApiKey = googleApiKey;

    client = new Safebrowsing.Builder(
        GoogleNetHttpTransport.newTrustedTransport(),
        JacksonFactory.getDefaultInstance(),
        null).build();
  }

  /**
   * Returns a threat designator for a URL. A site is malicious if the Safebrowsing API returns
   * threats of any type for any platform.
   *
   * <p>This is NOT CACHED because threat information is always changing.
   *
   * @param url - the URL to check
   * @return a ThreatStatus for the URL, UNKNOWN if an exception occurred on lookup.
   */
  public ThreatStatus threatStatus(final String url) {
    Objects.requireNonNull(url);
    LOG.debug("Looking up safebrowsing status of {}", url);

    try {
      // We construct a ThreatInfo instance which tells the service which kind of threats we're
      // looking for.
      final ThreatInfo ti = new ThreatInfo();
      ti.setThreatTypes(Arrays.asList("MALWARE", "SOCIAL_ENGINEERING", "UNWANTED_SOFTWARE",
          "POTENTIALLY_HARMFUL_APPLICATION"));
      ti.setPlatformTypes(Collections.singletonList("ANY_PLATFORM"));

      final FindThreatMatchesRequest request = new FindThreatMatchesRequest();
      request.setClient(new ClientInfo());
      request.setThreatInfo(ti);

      final ThreatEntry te = new ThreatEntry();
      te.setUrl(url);
      ti.setThreatEntries(Collections.singletonList(te));

      final FindThreatMatchesResponse findThreatMatchesResponse = client
          .threatMatches()
          .find(request)
          .setKey(googleApiKey)
          .execute();

      final List<ThreatMatch> threatMatches = findThreatMatchesResponse.getMatches();
      if (threatMatches == null || threatMatches.size() == 0) {
        return ThreatStatus.BENIGN;
      } else {
        return ThreatStatus.MALICIOUS;
      }
    } catch (IOException ex) {
      LOG.error("couldn't get safebrowsing status", ex);
      return ThreatStatus.UNKNOWN;
    }
  }
}
