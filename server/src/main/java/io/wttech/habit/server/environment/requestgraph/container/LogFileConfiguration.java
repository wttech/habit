package io.wttech.habit.server.environment.requestgraph.container;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.List;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE, isGetterVisibility = NONE)
class LogFileConfiguration {

  private final String serverName;
  private final List<String> logFiles;

  LogFileConfiguration(String serverName, List<String> logFiles) {
    this.serverName = serverName;
    this.logFiles = logFiles;
  }

  public String getServerName() {
    return serverName;
  }

  public List<String> getLogFiles() {
    return logFiles;
  }

}
