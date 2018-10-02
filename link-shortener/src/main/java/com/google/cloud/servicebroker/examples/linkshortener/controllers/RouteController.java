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
package com.google.cloud.servicebroker.examples.linkshortener.controllers;

import com.google.cloud.servicebroker.examples.linkshortener.domain.Link;
import com.google.cloud.servicebroker.examples.linkshortener.domain.LinkInfo;
import com.google.cloud.servicebroker.examples.linkshortener.services.LinkInfoService;
import com.google.cloud.servicebroker.examples.linkshortener.repositories.LinkRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contains the controller code for the main HTTP handlers.
 */
@Controller
@RequestMapping("/")
public class RouteController {

  @Autowired
  private LinkRepository linkRepo;

  @Autowired
  private LinkInfoService infoService;

  /**
   * Generates a redirect or a page displaying the short-url. If the page does not exist, the user
   * is asked to edit it. If the page is internal, the user is redirected. If the page is external
   * and malicious, the page is blocked. If the page is external and benign, a warning and preview
   * of the page is shown before the user goes there.
   *
   * @param stub The stub the user was linked to.
   * @param model The model of the page the link is added to if we need to render an interstitial.
   * @return A redirect or template to load.
   */
  @GetMapping("/{stub}")
  public String lookupSite(@PathVariable("stub") String stub, Model model) {
    final Optional<Link> repo = linkRepo.findById(stub);
    if (!repo.isPresent()) {
      return "redirect:/?edit=" + stub;
    }

    final LinkInfo linkInfo = infoService.getLinkInfo(repo.get());
    if (linkInfo.isLocal()) {
      return "redirect:" + repo.get().getUrl();
    }

    model.addAttribute("link", repo.get());

    switch (linkInfo.getThreatStatus()) {
      case MALICIOUS:
        return "blocked";
      case BENIGN:
        return "block-external";
      default:
        return "scanner-down";
    }
  }

  /**
   * Get the index page for describing what the site is and how to create a new URL.
   *
   * @return the index page template name.
   */
  @GetMapping("/")
  public String index() {
    return "index";
  }
}
