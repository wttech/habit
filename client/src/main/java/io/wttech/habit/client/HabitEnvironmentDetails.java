package io.wttech.habit.client;

public class HabitEnvironmentDetails {

  private final String hostname;
  private final int httpPort;
  private final String environmentName;

  HabitEnvironmentDetails(String hostname, int httpPort,
      String environmentName) {
    this.hostname = hostname;
    this.httpPort = httpPort;
    this.environmentName = environmentName;
  }

  public static HabitEnvironmentDetails of(String hostname, int httpPort,
      String environmentName) {
    return new HabitEnvironmentDetails(hostname, httpPort, environmentName);
  }

  public String getHostname() {
    return hostname;
  }

  public String getEnvironmentName() {
    return environmentName;
  }

  public int getHttpPort() {
    return httpPort;
  }

}
