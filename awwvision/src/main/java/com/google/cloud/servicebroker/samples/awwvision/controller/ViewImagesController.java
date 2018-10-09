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

import com.google.cloud.servicebroker.samples.awwvision.service.CuteImageService;
import com.google.cloud.storage.Blob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Provides request mappings for viewing cute images.
 */
@Controller
public class ViewImagesController {

  @Autowired
  private CuteImageService cuteImageService;

  /**
   * Renders the home page.
   *
   * @param model The Model object.
   * @return The home page view name.
   * @throws IOException If the StorageApi cannot list objects.
   */
  @RequestMapping("/")
  public String view(Model model) throws IOException {
    List<Blob> objects = cuteImageService.listAll();
    List<Image> images = new ArrayList<>();
    for (Blob obj : objects) {
      Image image = new Image(getPublicUrl(cuteImageService.getBucketName(), obj.getName()),
          obj.getMetadata().get("label"));
      images.add(image);
    }
    model.addAttribute("images", images);
    return "index";
  }

  /**
   * Renders a specifc label's page.
   *
   * @param label The label.
   * @param model The Model object.
   * @return The view to render.
   * @throws IOException If the StorageApi cannot list objects.
   */
  @RequestMapping("/label/{label}")
  public String viewLabel(@PathVariable("label") String label, Model model)
      throws IOException {
    List<Blob> objects = cuteImageService.listAll();
    List<Image> images = new ArrayList<>();
    for (Blob obj : objects) {
      Image image = new Image(getPublicUrl(cuteImageService.getBucketName(), obj.getName()),
          obj.getMetadata().get("label"));
      if (image.label.equals(label)) {
        images.add(image);
      }
    }
    model.addAttribute("images", images);
    return "index";
  }

  static String getPublicUrl(String bucket, String object) {
    return String.format("http://storage.googleapis.com/%s/%s", bucket, object);
  }

  static class Image {

    private String url;
    private String label;

    public Image(String url, String label) {
      this.url = url;
      this.label = label;
    }

    public String getUrl() {
      return url;
    }

    public String getLabel() {
      return label;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null || getClass() != obj.getClass()) {
        return false;
      }
      Image image = (Image) obj;
      return Objects.equals(url, image.url) && Objects.equals(label, image.label);
    }

    @Override
    public int hashCode() {
      return Objects.hash(url, label);
    }

    @Override
    public String toString() {
      return "Image{" + "url='" + url + '\''
          + ", label='" + label + '\''
          + '}';
    }
  }
}
