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

package com.google.cloud.servicebroker.samples.storelocator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.google.cloud.servicebroker.samples.storelocator.data.LocationBounds;
import com.google.cloud.servicebroker.samples.storelocator.data.Store;
import com.google.cloud.servicebroker.samples.storelocator.repository.StoreRepository;
import com.google.cloud.spanner.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.cloud.gcp.data.spanner.core.SpannerOperations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class StoreRepositoryTest {

  private static final Store store1 = new Store(0, 0, "1", "1", null, "6:00 AM", "6:00 PM", "(555) 555-5555");
  private static final Store store2 = new Store(-1, -1, "1", "1", null, "6:00 AM", "6:00 PM", "(555) 555-5555");
  private static final Store store3 = new Store(1, 1, "1", "1", null, "6:00 AM", "6:00 PM", "(555) 555-5555");
  private static final Store store4 = new Store(-1, 1, "1", "1", null, "6:00 AM", "6:00 PM", "(555) 555-5555");
  private static final Store store5 = new Store(1, -1, "1", "1", null, "6:00 AM", "6:00 PM", "(555) 555-5555");

  private static final LocationBounds query1 = new LocationBounds(-1, -1, 1, 1);
  private static final LocationBounds query2 = new LocationBounds(-2, -2, 2, 2);

  private static final Statement statement1 = Statement.newBuilder("SELECT * FROM stores WHERE latitude BETWEEN @lat1 AND @lat2 AND longitude BETWEEN @lng1 AND @lng2")
      .bind("lat1").to(-1.0)
      .bind("lat2").to(1.0)
      .bind("lng1").to(-1.0)
      .bind("lng2").to(1.0)
      .build();

  private static final Statement statement2 = Statement.newBuilder("SELECT * FROM stores WHERE latitude BETWEEN @lat1 AND @lat2 AND longitude BETWEEN @lng1 AND @lng2")
      .bind("lat1").to(-2.0)
      .bind("lat2").to(2.0)
      .bind("lng1").to(-2.0)
      .bind("lng2").to(2.0)
      .build();

  private static final List<Store> result1 = Collections.singletonList(store1);
  private static final List<Store> result2 = Arrays.asList(store1, store2, store3, store4, store5);

  @MockBean
  StoreRepository storeRepository;

  @MockBean
  SpannerOperations spannerOperations;

  @SpyBean
  StoreService storeService;

  @Test
  public void testBuildStatement() {
    assertThat(storeService.buildStatement(query1)).isEqualTo(statement1);
    assertThat(storeService.buildStatement(query2)).isEqualTo(statement2);
  }

  @Test
  public void testFindWithinBounds() {

    assertThat(storeRepository).isNotNull();

    when(storeRepository.getSpannerTemplate()).thenReturn(spannerOperations);

    given(spannerOperations.query(Store.class, statement1)).willReturn(result1);
    assertThat(storeService.findWithinBounds(query1)).isEqualTo(result1);

    given(spannerOperations.query(Store.class, statement2)).willReturn(result2);
    assertThat(storeService.findWithinBounds(query2)).isEqualTo(result2);
  }

}
