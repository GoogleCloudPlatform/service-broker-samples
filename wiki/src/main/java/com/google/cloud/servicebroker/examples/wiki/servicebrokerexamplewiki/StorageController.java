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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/", produces=MediaType.APPLICATION_JSON_VALUE)
public class StorageController {

  private static final Logger LOGGER = LoggerFactory.getLogger(StorageController.class);

  @GetMapping("/status")
  public Map<String, Object> getStatus() {
    final Map<String, Object> outputMap = new HashMap<>();

    outputMap.put("username", "guest");
    outputMap.put("space", Collections.singletonMap("recipe", "all"));

    return outputMap;
  }

  @PutMapping("/recipes/all/tiddlers/{title}")
  public void saveTiddler(@PathVariable String title, @RequestBody Tiddler tiddler) {
    LOGGER.info("Updating tiddler {}", title);
    // if !mustBeAdmin(w, r) {
    //   return
    // }
    // ctx := appengine.NewContext(r)
    // key := datastore.NewKey(ctx, "Tiddler", title, 0, nil)
    // data, err := ioutil.ReadAll(r.Body)
    // if err != nil {
    //   http.Error(w, "cannot read data", 400)
    //   return
    // }
    // var js map[string]interface{}
    // err = json.Unmarshal(data, &js)
    // if err != nil {
    //   http.Error(w, err.Error(), 500)
    //   return
    // }
    //
    // js["bag"] = "bag"
    //
    // rev := 1
    // var old Tiddler
    // if err := datastore.Get(ctx, key, &old); err == nil {
    //   rev = old.Rev + 1
    // }
    // js["revision"] = rev
    //
    // var t Tiddler
    // text, ok := js["text"].(string)
    // if ok {
    //   t.Text = text
    // }
    // delete(js, "text")
    // t.Rev = rev
    // meta, err := json.Marshal(js)
    // if err != nil {
    //   http.Error(w, err.Error(), 500)
    //   return
    // }
    // t.Meta = string(meta)
    // _, err = datastore.Put(ctx, key, &t)
    // if err != nil {
    //   http.Error(w, err.Error(), 500)
    //   return
    // }
    //
    // key2 := datastore.NewKey(ctx, "TiddlerHistory", title+"#"+fmt.Sprint(t.Rev), 0, nil)
    // if _, err := datastore.Put(ctx, key2, &t); err != nil {
    //   http.Error(w, err.Error(), 500)
    //   return
    // }
    //
    // etag := fmt.Sprintf("\"bag/%s/%d:%x\"", url.QueryEscape(title), rev, md5.Sum(data))
    // w.Header().Set("Etag", etag)

  }

  @GetMapping("/recipes/all/tiddlers/{title}")
  public Tiddler getTiddler(@PathVariable String title) {
    LOGGER.info("Getting tiddler {}", title);

    return new Tiddler();
  }


  @GetMapping("/recipes/all/tiddlers.json")
  public List<Tiddler> getTiddlersJson() {
    LOGGER.info("Getting tiddlers JSON List");

    return Collections.emptyList();
  }


  @DeleteMapping("/bags/bag/tiddlers/{title}")
  public void deleteTiddler(@PathVariable String title) {
    LOGGER.info("Deleting tiddler {}", title);

    // TODO the user must be admin to call this method

    // ctx := appengine.NewContext(r)
    // key := datastore.NewKey(ctx, "Tiddler", title, 0, nil)
    // var t Tiddler
    // if err := datastore.Get(ctx, key, &t); err != nil {
    //   http.Error(w, err.Error(), 500)
    //   return
    // }
    // t.Rev++
    // t.Meta = ""
    // t.Text = ""
    // if _, err := datastore.Put(ctx, key, &t); err != nil {
    //   http.Error(w, err.Error(), 500)
    //   return
    // }
    // key2 := datastore.NewKey(ctx, "TiddlerHistory", title+"#"+fmt.Sprint(t.Rev), 0, nil)
    // if _, err := datastore.Put(ctx, key2, &t); err != nil {
    //   http.Error(w, err.Error(), 500)
    //   return
    // }
  }
}
