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

package com.google.cloud.servicebroker.samples.storelocator.config;

import com.google.common.base.Strings;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;

/**
 * Spring config {@link Condition} for Cloud Foundry deployments.
 *
 * Checks for the "VCAP_APPLICATION" environment variable, which is assumed to exist in
 * Cloud Foundry app environments.
 *
 * Use as the target class on {@link Conditional} configurations.
 */
public class CloudFoundryDeploymentCondition implements Condition {

  @Override
  public boolean matches(@NonNull ConditionContext context,
      @NonNull AnnotatedTypeMetadata metadata) {
    return !Strings.isNullOrEmpty(System.getenv("VCAP_APPLICATION"));
  }
}
