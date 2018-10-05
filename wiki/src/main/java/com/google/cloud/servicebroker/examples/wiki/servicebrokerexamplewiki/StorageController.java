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
package com.google.cloud.servicebroker.examples.wiki.servicebrokerexamplewiki;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/")
public class StorageController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StorageController.class);

  @Autowired
  private TiddlerRepository tiddlerRepository;

  @GetMapping(value="/status", produces={MediaType.APPLICATION_JSON_VALUE})
  public Map<String, Object> getStatus() {
    final Map<String, Object> outputMap = new HashMap<>();

    outputMap.put("username", "guest");
    outputMap.put("space", Collections.singletonMap("recipe", "all"));

    return outputMap;
  }

  // TODO add spring security annotations for write method
  @PutMapping(value = "/recipes/all/tiddlers/{title}")
  public void saveTiddler(@PathVariable final String title, @RequestBody final Tiddler tiddler, HttpServletResponse response)
      throws JsonProcessingException {
    LOGGER.info("Updating tiddler {}", title);

    tiddler.setId(titleToId(title));
    tiddlerRepository.save(tiddler);

    final String objStr = new ObjectMapper().writeValueAsString(tiddler);
    response.addHeader("Etag", String.format("\"bag/%s:%d\"", titleToId(title), objStr.hashCode()));
  }

  @GetMapping(value="/recipes/all/tiddlers/{title}", produces={MediaType.APPLICATION_JSON_VALUE})
  public Optional<Tiddler> getTiddler(@PathVariable String title) {
    LOGGER.info("Getting tiddler {}", title);

    return tiddlerRepository.findById(titleToId(title));
  }

  @GetMapping(value="/recipes/all/tiddlers.json", produces={MediaType.APPLICATION_JSON_VALUE})
  public List<Tiddler> getTiddlersJson() {
    LOGGER.info("Getting tiddlers JSON List");

    final List<Tiddler> out = new LinkedList<>();
    tiddlerRepository.findAll().forEach(out::add);
    return out;
  }


  // TODO add spring security annotations for write method
  @DeleteMapping("/bags/bag/tiddlers/{title}")
  public void deleteTiddler(@PathVariable String title) {
    LOGGER.info("Deleting tiddler {}", title);

    tiddlerRepository.deleteById(titleToId(title));
  }


  private String titleToId(String title) {
    return Base64.getUrlEncoder().encodeToString(title.getBytes());
  }
}
