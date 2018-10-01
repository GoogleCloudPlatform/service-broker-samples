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
package com.google.cloud.servicebroker.examples.linkshortener.beans;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * Link holds a stub to URL mapping.
 */
@Entity
@Table(name = "links")
public class Link {
  @Id
  @Column(name="stub")
  @Pattern(regexp = "^([a-zA-Z0-9]+(-[a-zA-Z0-9+])*)$")
  private String stub;

  @Column(name="url")
  @NotBlank
  @Size(max=1024)
  private String url;

  public Link(){}

  public String getStub() {
    return stub;
  }

  public void setStub(String stub) {
    this.stub = stub;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
