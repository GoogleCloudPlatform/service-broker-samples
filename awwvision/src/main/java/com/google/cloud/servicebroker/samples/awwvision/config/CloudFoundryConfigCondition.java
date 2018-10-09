package com.google.cloud.servicebroker.samples.awwvision.config;

import com.google.common.base.Strings;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.annotation.Nonnull;

public class CloudFoundryConfigCondition implements Condition {

  @Override
  public boolean matches(@Nonnull ConditionContext context,
      @Nonnull AnnotatedTypeMetadata metadata) {
    return !Strings.isNullOrEmpty(System.getenv("VCAP_SERVICES"));
  }
}
