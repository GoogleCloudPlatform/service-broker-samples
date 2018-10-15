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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.model.StorageObject;
import com.google.cloud.servicebroker.samples.awwvision.MockableImageAnnotatorClient;
import com.google.cloud.servicebroker.samples.awwvision.controller.ViewImagesController.Image;
import com.google.cloud.servicebroker.samples.awwvision.service.CuteImageService;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobInfo;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(properties = {"gcp-storage-bucket=fake-bucket"})
public class ViewImagesControllerTest {

  @MockBean
  MockableImageAnnotatorClient vision;

  @MockBean
  Storage storageService;

  // Even though this is not used directly in the test, mock it out so the application doesn't try
  // to read environment variables to set the credential.
  @MockBean
  GoogleCredential googleCredential;

  @MockBean
  CuteImageService cuteImageService;

  @Autowired
  private MockMvc mvc;

  private static final String BUCKET = "fake-bucket";

  @Before
  public void setup() throws Exception {
    BlobInfo obj1 =
        Blob.newBuilder(BUCKET, "obj1").setMetadata(ImmutableMap.of("label", "dog")).build();
    BlobInfo obj2 =
        Blob.newBuilder(BUCKET, "obj1").setMetadata(ImmutableMap.of("label", "cat")).build();

    when(cuteImageService.listAll()).thenReturn(Arrays.asList(obj1, obj2));
    when(cuteImageService.getBucketName()).thenReturn(BUCKET);
  }

  @Test
  public void testView() throws Exception {
    Image img1 = new Image(ViewImagesController.getPublicUrl(BUCKET, "obj1"), "dog");
    Image img2 = new Image(ViewImagesController.getPublicUrl(BUCKET, "obj2"), "cat");
    mvc.perform(get("/")).andExpect(model().attribute("images", containsInAnyOrder(img1, img2)));
  }

  @Test
  public void testViewLabel() throws Exception {
    Image dog = new Image(ViewImagesController.getPublicUrl(BUCKET, "obj1"), "dog");
    mvc.perform(get("/label/dog")).andExpect(model().attribute("images", contains(dog)));
  }

  @Test
  public void testViewLabelEmpty() throws Exception {
    mvc.perform(get("/label/octopus")).andExpect(model().attribute("images", empty()));
  }
}
