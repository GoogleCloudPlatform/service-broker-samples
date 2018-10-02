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

package com.google.cloud.servicebroker.samples.storelocator.data;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@JsonTest
public class LocationBoundsTest {

  @Autowired
  private JacksonTester<LocationBounds> json;

  private final LocationBounds jsonObject = new LocationBounds(99.9, 88.8, 123.4, -30.35);

  // lng1 and lng2 flipped because the first coordinate pair is always the lesser one.
  private final String jsonString = "{\"lat1\":99.9,\"lng1\":-30.35,\"lat2\":123.4,\"lng2\":88.8}";

  @Test
  public void testSerialize() throws Exception {
    assertThat(this.json.write(this.jsonObject)).isEqualToJson(jsonString);
  }

  @Test
  public void testDeserialize() throws Exception {

    // Also tests equals method
    assertThat(this.json.parse(this.jsonString)).isEqualTo(jsonObject);
  }

}
