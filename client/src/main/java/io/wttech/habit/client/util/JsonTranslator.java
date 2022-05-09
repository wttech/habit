package io.wttech.habit.client.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.UncheckedIOException;

public class JsonTranslator {

  private final ObjectMapper objectMapper;

  public JsonTranslator() {
    this.objectMapper = new ObjectMapper();
    this.objectMapper.registerModule(new JavaTimeModule());
  }

  public static JsonTranslator instance() {
    return new JsonTranslator();
  }

  public <T> T toClass(JsonNode jsonNode, Class<T> clazz) {
    try {
      return objectMapper.readValue(jsonNode.traverse(), clazz);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public <T> T toClass(String json, Class<T> clazz) {
    try {
      return objectMapper.readValue(json, clazz);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public String asString(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new IllegalArgumentException(e);
    }
  }

}
