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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.cloud.servicebroker.samples.awwvision.data.RedditResponse;
import com.google.cloud.servicebroker.samples.awwvision.data.RedditResponse.Data;
import com.google.cloud.servicebroker.samples.awwvision.data.RedditResponse.Image;
import com.google.cloud.servicebroker.samples.awwvision.data.RedditResponse.Listing;
import com.google.cloud.servicebroker.samples.awwvision.data.RedditResponse.ListingData;
import com.google.cloud.servicebroker.samples.awwvision.data.RedditResponse.Preview;
import com.google.cloud.servicebroker.samples.awwvision.data.RedditResponse.Source;
import com.google.cloud.servicebroker.samples.awwvision.service.CuteImageService;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(properties = {"gcp-storage-bucket=fake-bucket"})
public class RedditScraperControllerTest {

  @MockBean
  Vision vision;

  @MockBean
  CuteImageService cuteImageService;

  // Even though this is not used directly in the test, mock it out so the application doesn't try
  // to read environment variables to set the credential.
  @MockBean
  GoogleCredential googleCredential;

  @SpyBean
  RedditScraperController scraper;

  @Before
  public void setup() throws Exception {

    // Have the Vision API return "dog" for any request.
    Vision.Images images = Mockito.mock(Vision.Images.class);
    Vision.Images.Annotate annotate = Mockito.mock(Vision.Images.Annotate.class);
    when(vision.images()).thenReturn(images);
    when(images.annotate(ArgumentMatchers.any(BatchAnnotateImagesRequest.class))).thenReturn(annotate);
    when(annotate.execute()).thenReturn(
        new BatchAnnotateImagesResponse().setResponses(ImmutableList.of(new AnnotateImageResponse()
            .setLabelAnnotations(ImmutableList.of(new EntityAnnotation().setDescription("dog"))))));

    doReturn("".getBytes()).when(scraper).download(ArgumentMatchers.any(URL.class));
  }

  @Test
  public void testScrape() throws Exception {
    Image img1 = new Image(new Source("http://url1.com"), "img1");
    Image img2 = new Image(new Source("http://url2.com"), "img2");
    RedditResponse redditResponse = new RedditResponse(new Data(
        new Listing[]{new Listing(new ListingData(new Preview(new Image[]{img1, img2})))}));
    redditResponse.data.children[0].data.url = "http://listing-url.com";

    scraper.storeAndLabel(redditResponse);

    verify(cuteImageService).uploadJpeg("http://listing-url.com",
        new URL("http://listing-url.com"), ImmutableMap.of("label", "dog"));
    verify(cuteImageService).uploadJpeg("http://listing-url.com",
        new URL("http://listing-url.com"), ImmutableMap.of("label", "dog"));
  }
}
