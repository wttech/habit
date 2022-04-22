package io.wttech.habit.client;

public class HabitSettings {

  private final String rootFolder;
  private final String environmentName;
  private final String hostname;
  private final Integer httpPort;
  private final String configurationRelativePath;

  public HabitSettings(String rootFolder, String environmentName, String hostname,
      Integer httpPort, String configurationRelativePath) {
    this.rootFolder = rootFolder;
    this.environmentName = environmentName;
    this.hostname = hostname;
    this.httpPort = httpPort;
    this.configurationRelativePath = configurationRelativePath;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static HabitSettings builtin() {
    return HabitSettings.builder().build();
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

  public String getConfigurationRelativePath() {
    return configurationRelativePath;
  }

  public boolean isRootFolderSpecified() {
    return rootFolder != null;
  }

  public boolean isEnvironnmentNameSpecified() {
    return environmentName != null;
  }

  public boolean isHostnameSpecified() {
    return hostname != null;
  }

  public boolean isHttpPortSpecified() {
    return httpPort != null;
  }

  public boolean isConfigurationRelativePathSpecified() {
    return configurationRelativePath != null;
  }

  public static final class Builder {

    private String rootFolder;
    private String environmentName;
    private String hostname;
    private Integer httpPort;
    private Integer httpsPort;
    private String configurationRelativePath;

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

    public Builder withHttpPort(Integer httpPort) {
      this.httpPort = httpPort;
      return this;
    }

    public Builder withConfigurationRelativePath(String configurationRelativePath) {
      this.configurationRelativePath = configurationRelativePath;
      return this;
    }

    public HabitSettings build() {
      return new HabitSettings(rootFolder, environmentName, hostname, httpPort,
          configurationRelativePath);
    }
  }
}
