package io.wttech.habit.server.docker.container;

import io.wttech.habit.server.docker.DockerClientException;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContainerService {

  private final DockerClient dockerClient;
  private final com.github.dockerjava.api.DockerClient dockerJavaClient;

  public String createContainer(String containerName, ContainerConfig config) {
    log.info("Creating container {}", containerName);
    try {
      ContainerCreation containerCreation = dockerClient.createContainer(config, containerName);
      return containerCreation.id();
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

  public void start(String containerId) {
    log.info("Starting container {}", containerId);
    try {
      dockerClient.startContainer(containerId);
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

  public void destroy(String containerId) {
    log.info("Destroying container {}", containerId);
    try {
      if (dockerClient.inspectContainer(containerId).state().running()) {
        dockerClient.killContainer(containerId);
      }
      dockerClient.removeContainer(containerId);
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

  public void restartContainer(String containerId) {
    log.info("Restarting container {}", containerId);
    try {
      dockerClient.restartContainer(containerId);
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

  public void copy(Path hostPath, String containerId, String containerPath) {
    log.info("Resource copy from {} to container {} on path {}", hostPath, containerId,
        containerPath);
    try {
      dockerClient.copyToContainer(hostPath, containerId, containerPath);
    } catch (DockerException | InterruptedException | IOException e) {
      throw new DockerClientException(e);
    }
  }

  public boolean isRunning(String containerId) {
    try {
      return dockerClient.inspectContainer(containerId).state().running();
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

  public CommandResult exec(String containerName, String... command) {
    log.info("Executing command {} on container {}", command, containerName);
    try {
      ExecCreateCmdResponse createdExec = dockerJavaClient.execCreateCmd(containerName)
          .withCmd(command)
          .withAttachStdout(true)
          .withAttachStderr(true)
          .exec();
      ByteArrayOutputStream logOutput = new ByteArrayOutputStream();
      ExecStartResultCallback callback = new ExecStartResultCallback(logOutput, logOutput);
      dockerJavaClient.execStartCmd(createdExec.getId()).exec(callback).awaitCompletion();
      Integer exitCode = dockerJavaClient.inspectExecCmd(createdExec.getId()).exec().getExitCode();
      return CommandResult.of(logOutput.toString(StandardCharsets.UTF_8.name()), exitCode);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  public StreamingCommand execStreaming(String containerName, String... command) {
    log.info("Executing streaming command {} on container {}", command, containerName);
    ExecCreateCmdResponse createdExec = dockerJavaClient.execCreateCmd(containerName)
        .withCmd(command)
        .withAttachStdout(true)
        .withAttachStderr(true)
        .exec();
    EmitterProcessor<byte[]> processor = EmitterProcessor.create(false);
    CommandOutputStream callback = new CommandOutputStream(processor.sink());
    dockerJavaClient.execStartCmd(createdExec.getId()).exec(callback);
    return StreamingCommand.of(callback, processor);
  }

  public String getLogs(String containerName) {
    try {
      ByteArrayOutputStream logOutput = new ByteArrayOutputStream();
      ExecStartResultCallback callback = new ExecStartResultCallback(logOutput, logOutput);
      dockerJavaClient.logContainerCmd(containerName)
          .withStdErr(true)
          .withStdOut(true)
          .withFollowStream(false)
          .withTailAll()
          .exec(callback)
          .awaitCompletion();
      return logOutput.toString(StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
