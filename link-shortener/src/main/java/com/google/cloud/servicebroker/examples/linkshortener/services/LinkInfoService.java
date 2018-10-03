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

import com.google.cloud.servicebroker.examples.linkshortener.domain.Link;
import com.google.cloud.servicebroker.examples.linkshortener.domain.LinkInfo;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * LinkInfoService gets basic information about a URL.
 */
@Service
public class LinkInfoService {

  private static final Logger LOG = LoggerFactory.getLogger(LinkInfoService.class);

  @Value("${shortener.internal.domain}")
  private String allowedUrlSuffix;

  @Autowired
  private SafebrowsingService safebrowsingService;

  /**
   * Get information about the given link.
   *
   * @param link the link to get information for
   * @return data to augment the link, if the link is not local.
   */
  public LinkInfo getLinkInfo(final Link link) {
    Objects.requireNonNull(link);

    final String url = link.getUrl();

    final LinkInfo output = new LinkInfo();
    output.setLocal(isLocal(url));

    if (!output.isLocal()) {
      output.setThreatStatus(safebrowsingService.threatStatus(url));
    }

    return output;
  }

  /**
   * isLocal determines if the given URL is local to the domain (ends with allowedUrlSuffix).
   *
   * @param url the URL to check.
   * @return true if the url ends with allowedUrlSuffix, false if the URL is bad or does not.
   */
  private boolean isLocal(final String url) {
    Objects.requireNonNull(url);

    try {
      return new URL(url)
          .getHost()
          .endsWith(allowedUrlSuffix);
    } catch (MalformedURLException e) {
      LOG.error("couldn't parse URL", e);
      return false; // return false for safety
    }
  }
}
