package io.wttech.habit.gradle.server;

import io.wttech.habit.gradle.util.FileRetriever;
import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerInstaller {

  private static final Logger LOGGER = LoggerFactory.getLogger(ServerInstaller.class);

  private final FileRetriever fileRetriever = FileRetriever.instance();

  public void provision(ProvisionDetails details) {
    String dockerComposeContent = fileRetriever.readDockerCompose();
    try {
      ProcessBuilder builder = new ProcessBuilder();
      builder.command("docker", "stack", "deploy", "--compose-file", "-", "habit");
      Map<String, String> environment = builder.environment();
      environment.put("SERVER_IMAGE", details.getServerImage());
      environment.put("HABIT_HTTP_PORT", Integer.toString(details.getHttpPort()));
      Process process = builder.start();
      OutputStream dockerCommandStream = process.getOutputStream();
      PrintWriter writer = new PrintWriter(dockerCommandStream);
      writer.println(dockerComposeContent);
      writer.println();
      writer.flush();
      writer.close();
      process.waitFor();
      process.destroy();
    } catch (InterruptedException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void provision() {
    provision(ProvisionDetails.builder().build());
  }

  public void shutdown() {
    try {
      ProcessBuilder builder = new ProcessBuilder();
      builder.command("docker", "stack", "rm", "habit");
      Process process = builder.start();
      process.waitFor();
      process.destroy();
    } catch (InterruptedException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean isDockerInstalled() {
    return getExitCode(Lists.newArrayList("docker", "--version")) == 0;
  }

  public boolean isDockerSwarmEnabled() {
    List<String> output = getOutputLines(Lists.newArrayList("docker", "info"));
    return output.stream().anyMatch(line -> line.contains("Swarm: active"));
  }

  private List<String> getOutputLines(List<String> command) {
    try {
      ProcessBuilder builder = new ProcessBuilder();
      builder.command(command);
      Process process = builder.start();
      BufferedReader bufferedReader = new BufferedReader(
          new InputStreamReader(process.getInputStream()));
      List<String> result = bufferedReader.lines().collect(Collectors.toList());
      process.waitFor();
      process.destroy();
      return result;
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private int getExitCode(List<String> command) {
    try {
      ProcessBuilder builder = new ProcessBuilder();
      builder.command(command);
      Process process = builder.start();
      int exitCode = process.waitFor();
      process.destroy();
      return exitCode;
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

}
