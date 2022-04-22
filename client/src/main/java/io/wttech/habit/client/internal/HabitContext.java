package io.wttech.habit.client.internal;

import io.wttech.habit.client.configuration.ServerConfiguration;

import java.util.List;

public class HabitContext {

  private final String rootFolder;
  private final String environmentName;
  private final String hostname;
  private final int httpPort;
  private final String configurationJson;
  private final List<ServerConfiguration> servers;

  HabitContext(String rootFolder, String environmentName, String hostname,
      int httpPort, String configurationJson,
      List<ServerConfiguration> servers) {
    this.rootFolder = rootFolder;
    this.environmentName = environmentName;
    this.hostname = hostname;
    this.httpPort = httpPort;
    this.configurationJson = configurationJson;
    this.servers = servers;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getRootFolder() {
    return rootFolder;
  }

  public String getEnvironmentName() {
    return environmentName;
  }

  public String getHostname() {
    return hostname;
  }

  public int getHttpPort() {
    return httpPort;
  }

  public String getConfigurationJson() {
    return configurationJson;
  }

  public List<ServerConfiguration> getServers() {
    return servers;
  }

  public static final class Builder {

    private String rootFolder;
    private String environmentName;
    private String hostname;
    private int httpPort;
    private String configurationJson;
    private List<ServerConfiguration> servers;

    private Builder() {
    }

    public Builder withRootFolder(String rootFolder) {
      this.rootFolder = rootFolder;
      return this;
    }

    public Builder withEnvironmentName(String environmentName) {
      this.environmentName = environmentName;
      return this;
    }

    public Builder withHostname(String hostname) {
      this.hostname = hostname;
      return this;
    }

    public Builder withConfigurationJson(String configurationJson) {
      this.configurationJson = configurationJson;
      return this;
    }

    public Builder withServers(List<ServerConfiguration> servers) {
      this.servers = servers;
      return this;
    }

    public Builder withHttpPort(int habitHttpPort) {
      this.httpPort = habitHttpPort;
      return this;
    }

    public HabitContext build() {
      return new HabitContext(rootFolder, environmentName, hostname, httpPort,
          configurationJson, servers);
    }
  }
}
