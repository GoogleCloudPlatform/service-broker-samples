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

package com.google.cloud.servicebroker.examples.linkshortener.domain;

import com.google.cloud.servicebroker.examples.linkshortener.enums.ThreatStatus;

/**
 * LinkInfo holds augmented data for links.
 */
public class LinkInfo {

  private boolean local;
  private ThreatStatus threatStatus = ThreatStatus.UNKNOWN;

  public boolean isLocal() {
    return local;
  }

  public void setLocal(boolean local) {
    this.local = local;
  }

  public ThreatStatus getThreatStatus() {
    return threatStatus;
  }

  public void setThreatStatus(ThreatStatus threatStatus) {
    this.threatStatus = threatStatus;
  }
}
