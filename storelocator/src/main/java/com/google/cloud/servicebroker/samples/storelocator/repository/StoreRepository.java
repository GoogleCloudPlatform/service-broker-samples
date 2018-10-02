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

package com.google.cloud.servicebroker.samples.storelocator.repository;

import com.google.cloud.servicebroker.samples.storelocator.data.Store;
import com.google.cloud.spanner.Key;

import org.springframework.cloud.gcp.data.spanner.repository.SpannerRepository;

/**
 * Interface for looking up {@link Store}s in a Spanner database.
 */
public interface StoreRepository extends SpannerRepository<Store, Key> {

}
