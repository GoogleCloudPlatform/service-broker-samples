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

package com.google.cloud.servicebroker.samples.awwvision.service;

import com.google.cloud.servicebroker.samples.awwvision.MockableImageAnnotatorClient;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.protobuf.ByteString;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

/**
 * Component which labels images using the Google Cloud Vision API.
 */
@Component
public class ImageLabelingService {

  @Autowired
  private MockableImageAnnotatorClient vision;

  /**
   * Calls the Vision API to get a single label for the given image.
   *
   * @param bytes The image, in bytes
   * @return The label given to the image, or null if the request could not successfully label the
   *         image
   */
  public String labelImage(byte[] bytes) throws IOException {
    AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
        .setImage(Image.newBuilder().setContent(ByteString.copyFrom(bytes)))
        .addFeatures( Feature.newBuilder()
            .setType(Type.LABEL_DETECTION)
            .setMaxResults(1)
        ).build();
    return sendAndParseRequest(request);
  }

  private String sendAndParseRequest(AnnotateImageRequest request) throws IOException {
    AnnotateImageResponse response = sendRequest(request);
    if (response == null) {
      return null;
    }
    if (response.getLabelAnnotations(0) == null) {
      throw new IOException(response.getError() != null ? response.getError().getMessage()
          : "Unknown error getting image annotations");
    }
    return response.getLabelAnnotations(0).getDescription();
  }

  private AnnotateImageResponse sendRequest(AnnotateImageRequest request) {

    BatchAnnotateImagesResponse batchResponse
        = vision.mockableBatchAnnotateImages(Collections.singletonList(request));

    if (batchResponse == null) {
      return null;
    }

    return batchResponse.getResponses(0);
  }
}
