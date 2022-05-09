package io.wttech.habit.server.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JsonTranslator {

  private final ObjectMapper objectMapper;

  public <T> T toClass(String json, Class<T> clazz) {
    try {
      return objectMapper.readValue(json, clazz);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public <T> T toClass(JsonNode jsonNode, Class<T> clazz) {
    try {
      return objectMapper.readValue(jsonNode.traverse(), clazz);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public String asString(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

}
