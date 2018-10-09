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

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Storage;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Component which handles the storage of cute images from https://reddit.com/r/aww
 *
 * <p>Uses the Cloud Storage Bucket configured in the application properties.
 */
@Controller
public class CuteImageService {

  @Autowired
  private Storage storageService;

  private final String bucketName;

  CuteImageService() {
    String env = System.getenv("VCAP_SERVICES");

    this.bucketName =
        new JSONObject(env)
            .getJSONArray("google-storage")
            .getJSONObject(0)
            .getJSONObject("credentials")
            .getString("bucket_name");
  }

  public String getBucketName() {
    return bucketName;
  }

  /**
   * Uploads a JPEG image to Cloud Storage.
   *
   * @param name The name of the image
   * @param url A URL pointing to the image
   * @param metadata Metadata about the image
   */
  public void uploadJpeg(String name, URL url, Map<String, String> metadata)
      throws IOException {
    InputStreamContent contentStream = new InputStreamContent("image/jpeg", url.openStream());
    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
    Blob objectMetadata = new Blob().setName(name)
        .setAcl(Collections
            .singletonList(new ObjectAccessControl().setEntity("allUsers").setRole("READER")))
        .setMetadata(metadata);

    storageService.create(this.bucketName, objectMetadata, contentStream).execute();
  }

  /**
   * Returns a List of all objects in the configured Cloud Storage bucket.
   *
   * @return A List of {@link Blob}.
   * @throws IOException If thrown by Google Storage calls.
   */
  public List<Blob> listAll() throws IOException {

    List<Blob> results = new ArrayList<>();
    Storage.ListBlobsOptions options = null;

    // Iterate through each page of results, and add them to our results list.
    do {
      results.appendAll(storage.list(bucketName, options);
    } while (null != options.getNextPageToken());

    return results;
  }

  /**
   * Gets a specific object in the configured Cloud Storage bucket.
   *
   * @param name The name of the object.
   * @return The Blob with the specified name, or null if one does not exist.
   */
  public Blob get(String name) {
    try {
      return storageService.get(BlobId.of(this.bucketName, name));
    } catch (IOException ignored) {
      return null;
    }
  }
}
