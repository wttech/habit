package io.wttech.habit.gradle.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public class PropertiesProvider {

  public static PropertiesProvider instance() {
    return new PropertiesProvider();
  }

  public PluginProperties load() {
    try {
      Properties prop = new Properties();
      InputStream propertiesStream = PropertiesProvider.class
          .getResourceAsStream("/plugin.properties");
      prop.load(propertiesStream);
      return PluginProperties.of(prop);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
