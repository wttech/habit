package io.wttech.habit.gradle.util;

import java.util.Properties;

public class PluginProperties {

  private final Properties properties;

  PluginProperties(Properties properties) {
    this.properties = properties;
  }

  public static PluginProperties of(Properties properties) {
    return new PluginProperties(properties);
  }

  public String getProxyImageId() {
    return properties.getProperty("proxyImage");
  }

  public String getServerImageId() {
    return properties.getProperty("serverImage");
  }

}
