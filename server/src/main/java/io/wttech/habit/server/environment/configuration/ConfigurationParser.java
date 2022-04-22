package io.wttech.habit.server.environment.configuration;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConfigurationParser {

  private final ObjectMapper objectMapper;

  public RootConfiguration parse(String json) {
    try {
      return objectMapper.readValue(json, RootConfiguration.class);
    } catch (JsonMappingException e) {
      throw new InvalidConfigurationException("Could not read environment configuration", e);
    } catch (JsonParseException e) {
      throw new InvalidConfigurationException("Invalid environment configuration JSON", e);
    } catch (IOException e) {
      throw new InvalidConfigurationException(
          "Unexpected error when reading environment configuration", e);
    }
  }

}
