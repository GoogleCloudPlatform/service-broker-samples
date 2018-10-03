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

package com.google.cloud.servicebroker.samples.storelocator.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.servicebroker.samples.storelocator.data.LocationBounds;
import com.google.cloud.servicebroker.samples.storelocator.data.Store;
import com.google.cloud.servicebroker.samples.storelocator.service.StoreService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class StoreControllerTest {

  private static final Store store1
      = new Store(0.0, 0.0, "1", "1", null, "6:00 AM", "6:00 PM", "(555) 555-5555");
  private static final Store store2
      = new Store(-1.0, -1.0, "1", "1", null, "6:00 AM", "6:00 PM", "(555) 555-5555");
  private static final Store store3
      = new Store(1.0, 1.0, "1", "1", null, "6:00 AM", "6:00 PM", "(555) 555-5555");
  private static final Store store4
      = new Store(-1.0, 1.0, "1", "1", null, "6:00 AM", "6:00 PM", "(555) 555-5555");
  private static final Store store5
      = new Store(1.0, -1.0, "1", "1", null, "6:00 AM", "6:00 PM", "(555) 555-5555");

  private static final LocationBounds query1 = new LocationBounds(-1, -1, 1, 1);
  private static final LocationBounds query2 = new LocationBounds(-2, -2, 2, 2);

  private static final List<Store> result1 = Collections.singletonList(store1);
  private static final List<Store> result2 = Arrays.asList(store1, store2, store3, store4, store5);

  @Autowired
  MockMvc mockMvc;

  @MockBean
  StoreService storeService;

  // Creates a real instance even though we don't perform any spying.
  @SpyBean
  StoreController storeController;

  @MockBean
  GoogleCredentials googleCredentials;

  @Test
  public void testFindWithinBounds_1() throws Exception {

    when(storeService.findWithinBounds(query1)).thenReturn(result1);

    mockMvc.perform(get("/api/v1/stores")
        .content(MediaType.ALL_VALUE)
        .param("lat1", Double.toString(query1.getLat1()))
        .param("lng1", Double.toString(query1.getLng1()))
        .param("lat2", Double.toString(query1.getLat2()))
        .param("lng2", Double.toString(query1.getLng2()))
    )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(content().json(
            "[{\"name\":\"1\",\"address\":\"1\",\"latitude\":0.0,\"longitude\":0.0}]"));
    ;
  }

  @Test
  public void testFindWithinBounds_2() throws Exception {

    when(storeService.findWithinBounds(query2)).thenReturn(result2);

    mockMvc.perform(get("/api/v1/stores")
        .content(MediaType.ALL_VALUE)
        .param("lat1", Double.toString(query2.getLat1()))
        .param("lng1", Double.toString(query2.getLng1()))
        .param("lat2", Double.toString(query2.getLat2()))
        .param("lng2", Double.toString(query2.getLng2()))
    )
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        .andExpect(content().json(
            "[{\"name\":\"1\",\"address\":\"1\",\"latitude\":0.0,\"longitude\":0.0},"
                + "{\"name\":\"1\",\"address\":\"1\",\"latitude\":-1.0,\"longitude\":-1.0},"
                + "{\"name\":\"1\",\"address\":\"1\",\"latitude\":1.0,\"longitude\":1.0},"
                + "{\"name\":\"1\",\"address\":\"1\",\"latitude\":-1.0,\"longitude\":1.0},"
                + "{\"name\":\"1\",\"address\":\"1\",\"latitude\":1.0,\"longitude\":-1.0}]"));
    ;
  }

  @Configuration
  static class Config {

    /**
     * Creates a CredentialsProvider using a mock credentials bean.
     *
     * <p>This allows the context to load.
     *
     * @param credentials mock credentials
     * @return a CredentialsProvider for the mock credentials.
     */
    @Bean
    public CredentialsProvider spannerCredentialsProvider(GoogleCredentials credentials) {
      return () -> credentials;
    }

    /**
     * Creates a RequestMappingHandlerAdapter which supports JSON
     *
     * <p>When using AutoConfigureMockMvc, a reflective instance of a RequestMappingHandlerAdapter
     * is instantiated. This means it will only be configured to use its default message converters,
     * which do not support JSON.
     *
     * <p>This is contrary to an actual launch of the application.
     * Normally, the WebMvcConfigurationSupport's RequestMappingHandlerAdapter bean is used, which
     * support JSON.
     *
     * @return a RequestMappingHandlerAdapter which supports JSON.
     */
    @Bean
    public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
      return new WebMvcConfigurationSupport().requestMappingHandlerAdapter();
    }

  }

}
