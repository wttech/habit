package io.wttech.habit.gradle.util;

import io.wttech.habit.gradle.server.ServerInstaller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.stream.Collectors;

public class FileRetriever {

  public static FileRetriever instance() {
    return new FileRetriever();
  }

  public String readDockerCompose() {
    return readFileContent("/docker-compose.yml");
  }

  private String readFileContent(String resourcePath) {
    try (InputStream composeFileStream = ServerInstaller.class
        .getResourceAsStream(resourcePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(composeFileStream))) {
      return reader.lines().collect(Collectors.joining(System.getProperty("line.separator")));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
