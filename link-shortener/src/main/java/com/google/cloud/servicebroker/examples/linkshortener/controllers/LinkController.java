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

import com.google.cloud.servicebroker.examples.linkshortener.beans.Link;
import com.google.cloud.servicebroker.examples.linkshortener.beans.LinkInfo;
import com.google.cloud.servicebroker.examples.linkshortener.services.LinkInfoService;
import com.google.cloud.servicebroker.examples.linkshortener.repositories.LinkRepository;
import com.google.cloud.servicebroker.examples.linkshortener.services.ScreenshotService;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contains controller code for the links.
 */
@RestController
@RequestMapping(value = "/api/v1/links", produces=MediaType.APPLICATION_JSON_VALUE)
public class LinkController {

  @Autowired
  private LinkRepository linkRepository;

  @Autowired
  private LinkInfoService linkInfoService;

  @Autowired
  private ScreenshotService screenshotService;

  @GetMapping("/{stub}")
  public Optional<Link> getLink(@PathVariable String stub) {
    return linkRepository.findById(stub);
  }

  @GetMapping("/{stub}/info")
  public Optional<LinkInfo> getInfo(@PathVariable String stub) {
    final Optional<Link> link = getLink(stub);

    if(link.isPresent()) {
      return Optional.of(linkInfoService.getLinkInfo(link.get()));
    }

    return Optional.empty();
  }

  @GetMapping("/{stub}/preview")
  public void getPreview(@PathVariable String stub, HttpServletResponse response)
      throws IOException {
    final Optional<Link> link = getLink(stub);
    if(!link.isPresent()) {
      response.sendError(404, "stub not found");
      return;
    }

    response.setContentType(MediaType.IMAGE_JPEG_VALUE);
    response.getOutputStream().write(screenshotService.getScreenshot(link.get().getUrl()));
  }

  @PostMapping("/")
  public Link createLink(@RequestBody Link link) {
    return linkRepository.save(link);
  }

  @DeleteMapping("/{stub}")
  public void deleteLink(@PathVariable String stub) {
    linkRepository.deleteById(stub);
  }
}
