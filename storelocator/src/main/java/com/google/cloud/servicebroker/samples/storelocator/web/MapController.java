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

package com.google.cloud.servicebroker.samples.storelocator.web;

import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Provides a templated landing page for the store locator application.
 *
 * <p>HTML resources are Thymeleaf templates, relying on the {@link ThymeleafAutoConfiguration}.
 */
@Controller
public class MapController {

  /**
   * Returns the index page, templated with Thymeleaf.
   *
   * @return the index page.
   */
  @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
  public String index() {
    return "index";
  }

}
