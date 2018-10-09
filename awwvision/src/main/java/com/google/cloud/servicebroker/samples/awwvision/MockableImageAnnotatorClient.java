package com.google.cloud.servicebroker.samples.awwvision;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.ImageAnnotatorClient;

import java.io.IOException;
import java.util.List;

/**
 * Wrapper around an ImageAnnotatorClient for unit testing purposes.
 *
 * <p>Mockito can not mock final methods.
 */
public class MockableImageAnnotatorClient extends ImageAnnotatorClient {

  public MockableImageAnnotatorClient(ImageAnnotatorClient client)
      throws IOException {
    super(client.getSettings());
  }

  public BatchAnnotateImagesResponse mockableBatchAnnotateImages(
      List<AnnotateImageRequest> requests) {
    return this.batchAnnotateImages(requests);
  }

}
