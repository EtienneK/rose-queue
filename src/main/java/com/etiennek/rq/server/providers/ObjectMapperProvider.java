package com.etiennek.rq.server.providers;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {

  private final ObjectMapper objectMapper;

  public ObjectMapperProvider() {
    objectMapper = create();
  }

  @Override
  public ObjectMapper getContext(Class<?> type) {
    return objectMapper;
  }

  private ObjectMapper create() {
    ObjectMapper result = new ObjectMapper();
    result.enable(SerializationFeature.INDENT_OUTPUT);
    result.registerModule(new GuavaModule());
    return result;
  }
}
