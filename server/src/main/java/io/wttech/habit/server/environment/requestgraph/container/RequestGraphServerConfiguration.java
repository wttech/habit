package io.wttech.habit.server.environment.requestgraph.container;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.List;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
class RequestGraphServerConfiguration {

  private final List<LogFileConfiguration> configurations;

  public RequestGraphServerConfiguration(
      List<LogFileConfiguration> configurations) {
    this.configurations = configurations;
  }

  public List<LogFileConfiguration> getConfigurations() {
    return configurations;
  }
}
