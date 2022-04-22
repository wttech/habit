package io.wttech.habit.junit;

public class HabitEnvironment {

  private final String id;
  private final String hostname;
  private final int httpPort;

  public HabitEnvironment(String id, String hostname, int httpPort) {
    this.id = id;
    this.hostname = hostname;
    this.httpPort = httpPort;
  }

  public static HabitEnvironment of(String name, String host, int httpPort) {
    return new HabitEnvironment(name, host, httpPort);
  }

  public String getId() {
    return id;
  }

  public String getHostname() {
    return hostname;
  }

  public String getHttpHost() {
    return hostname + ":" + httpPort;
  }

  public int getHttpPort() {
    return httpPort;
  }
}
