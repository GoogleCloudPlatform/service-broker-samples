package com.google.cloud.servicebroker.examples.wiki.servicebrokerexamplewiki;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gcp.data.datastore.core.convert.DatastoreCustomConversions;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.util.UrlPathHelper;

@SpringBootApplication
public class WikiApplication {

  public static void main(String[] args) {
    System.setProperty("org.apache.tomcat.util.buf.UDecoder.ALLOW_ENCODED_SLASH", "true");
    SpringApplication.run(WikiApplication.class, args);
  }

  @Bean
  public DatastoreCustomConversions datastoreCustomConversions() {
    return new DatastoreCustomConversions(
        Arrays.asList(
            new Converter<java.lang.String, java.util.LinkedHashMap<?, ?>>() {
              public java.util.LinkedHashMap<?, ?> convert(java.lang.String input) {
                try {
                  return new ObjectMapper().readValue(input, LinkedHashMap.class);
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              }
            },
            new Converter<LinkedHashMap<?, ?>, String>() {
              public String convert(LinkedHashMap<?, ?> input) {
                try {
                  return new ObjectMapper().writeValueAsString(input);
                } catch (JsonProcessingException e) {
                  throw new RuntimeException(e);
                }
              }
            }
        ));
  }
}
