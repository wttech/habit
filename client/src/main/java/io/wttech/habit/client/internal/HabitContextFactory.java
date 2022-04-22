package io.wttech.habit.client.internal;

import io.wttech.habit.client.ClientFacade;
import io.wttech.habit.client.ConfigurationFileLoader;
import io.wttech.habit.client.HabitSettings;
import io.wttech.habit.client.configuration.EnvironmentConfigurationModel;
import io.wttech.habit.client.util.JsonTranslator;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Supplier;

public class HabitContextFactory {

  private static final String ROOT_FOLDER_PROPERTY = "io.wttech.habit.rootFolder";
  private static final String ENVIRONMENT_NAME_PROPERTY = "io.wttech.habit.environmentId";
  private static final String CONFIGURATION_PATH_PROPERTY = "io.wttech.habit.configurationPath";
  private static final String HABIT_SERVER_HOSTNAME_PROPERTY = "io.wttech.habit.hostname";
  private static final String HABIT_SERVER_HTTP_PORT_PROPERTY = "io.wttech.habit.http.port";

  private static final String ROOT_FOLDER_ENVIRONMENTAL = "HABIT_ROOT_FOLDER";
  private static final String ENVIRONMENT_NAME_ENVIRONMENTAL = "HABIT_ENVIRONMENT_ID";
  private static final String CONFIGURATION_PATH_ENVIRONMENTAL = "HABIT_CONFIGURATION_PATH";
  private static final String HABIT_SERVER_HOSTNAME_ENVIRONMENTAL = "HABIT_HOSTNAME";
  private static final String HABIT_SERVER_HTTP_PORT_ENVIRONMENTAL = "HABIT_HTTP_PORT";

  private static final String CONFIGURATION_PATH = "./habit.json";
  private static final String DEFAULT_SERVER = "localhost";

  private final JsonTranslator jsonTranslator = JsonTranslator.instance();

  HabitContextFactory() {
  }

  public static HabitContextFactory instance() {
    return new HabitContextFactory();
  }

  public HabitContext getContext(HabitSettings settings) {
    String rootFolder = settings.isRootFolderSpecified()
        ? settings.getRootFolder()
        : readProperty(ROOT_FOLDER_PROPERTY, ROOT_FOLDER_ENVIRONMENTAL,
            () -> System.getProperty("user.dir"));
    String configurationRelativePath = settings.isConfigurationRelativePathSpecified()
        ? settings.getConfigurationRelativePath()
        : readProperty(CONFIGURATION_PATH_PROPERTY, CONFIGURATION_PATH_ENVIRONMENTAL,
            CONFIGURATION_PATH);
    Path configurationFilePath = Paths.get(rootFolder).resolve(configurationRelativePath);
    String habitServer = settings.isHostnameSpecified()
        ? settings.getHostname()
        : readProperty(HABIT_SERVER_HOSTNAME_PROPERTY, HABIT_SERVER_HOSTNAME_ENVIRONMENTAL,
            DEFAULT_SERVER);
    int habitHttpPort = settings.isHttpPortSpecified()
        ? settings.getHttpPort()
        : readIntegerProperty(HABIT_SERVER_HTTP_PORT_PROPERTY, HABIT_SERVER_HTTP_PORT_ENVIRONMENTAL,
            ClientFacade.DEFAULT_HTTP_PORT);

    String configurationString = new ConfigurationFileLoader()
        .load(getConfiguration(configurationFilePath.toString()));
    EnvironmentConfigurationModel configuration = jsonTranslator
        .toClass(configurationString, EnvironmentConfigurationModel.class);

    String environmentName = settings.isEnvironnmentNameSpecified()
        ? settings.getEnvironmentName()
        : readProperty(ENVIRONMENT_NAME_PROPERTY, ENVIRONMENT_NAME_ENVIRONMENTAL,
            () -> calculateDefaultProjectName(rootFolder, configuration.getId()));

    return HabitContext.builder()
        .withEnvironmentName(environmentName)
        .withHostname(habitServer)
        .withHttpPort(habitHttpPort)
        .withRootFolder(rootFolder)
        .withConfigurationJson(configurationString)
        .withServers(configuration.getServers())
        .build();
  }

  private URL getConfiguration(String configurationPath) {
    try {
      Path configurationFilePath = Paths.get(configurationPath);
      if (!Files.exists(configurationFilePath)) {
        throw new IllegalStateException(
            "Configuration file " + configurationPath + " does not exist");
      }
      return configurationFilePath.toUri().toURL();
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private String calculateDefaultProjectName(String rootFolder, String nameFromConfiguration) {
    if (nameFromConfiguration != null) {
      return nameFromConfiguration;
    }
    String folderName = Paths.get(rootFolder).getFileName().toString();
    return normalizeProjectName(folderName);
  }

  private String normalizeProjectName(String projectName) {
    return projectName.replaceAll("[^a-z_-]", "");
  }

  private String readProperty(String propertyName, String environmentalVariableName,
      String defaultValue) {
    Optional<String> systemPropertyValue = fromSystemProperties(propertyName);
    if (systemPropertyValue.isPresent()) {
      return systemPropertyValue.get();
    }
    Optional<String> environmentalVariableValue = fromEnvironmentalVariable(
        environmentalVariableName);
    if (environmentalVariableValue.isPresent()) {
      return environmentalVariableValue.get();
    }
    return defaultValue;
  }

  private int readIntegerProperty(String propertyName, String environmentalVariableName,
      int defaultValue) {
    Optional<String> systemPropertyValue = fromSystemProperties(propertyName);
    if (systemPropertyValue.isPresent()) {
      return Integer.parseInt(systemPropertyValue.get());
    }
    Optional<String> environmentalVariableValue = fromEnvironmentalVariable(
        environmentalVariableName);
    if (environmentalVariableValue.isPresent()) {
      return Integer.parseInt(environmentalVariableValue.get());
    }
    return defaultValue;
  }

  private String readProperty(String propertyName, String environmentalVariableName,
      Supplier<String> defaultValueSupplier) {
    Optional<String> systemPropertyValue = fromSystemProperties(propertyName);
    if (systemPropertyValue.isPresent()) {
      return systemPropertyValue.get();
    }
    Optional<String> environmentalVariableValue = fromEnvironmentalVariable(
        environmentalVariableName);
    if (environmentalVariableValue.isPresent()) {
      return environmentalVariableValue.get();
    }
    return defaultValueSupplier.get();
  }

  private int readIntegerProperty(String propertyName, String environmentalVariableName,
      Supplier<Integer> defaultValueSupplier) {
    Optional<String> systemPropertyValue = fromSystemProperties(propertyName);
    if (systemPropertyValue.isPresent()) {
      return Integer.parseInt(systemPropertyValue.get());
    }
    Optional<String> environmentalVariableValue = fromEnvironmentalVariable(
        environmentalVariableName);
    if (environmentalVariableValue.isPresent()) {
      return Integer.parseInt(environmentalVariableValue.get());
    }
    return defaultValueSupplier.get();
  }

  private Optional<String> fromSystemProperties(String propertyName) {
    return Optional.ofNullable(System.getProperty(propertyName));
  }

  private Optional<String> fromEnvironmentalVariable(String variableName) {
    return Optional.ofNullable(System.getenv(variableName));
  }

}
