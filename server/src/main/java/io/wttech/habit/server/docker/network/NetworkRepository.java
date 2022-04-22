package io.wttech.habit.server.docker.network;

import io.wttech.habit.server.docker.DockerClientException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerClient.ListNetworksParam;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Network;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NetworkRepository {

  private final DockerClient dockerClient;

  public Optional<Network> findFirstByProjectName(String projectName) {
    try {
      return dockerClient
          .listNetworks(
              ListNetworksParam.withLabel(NetworkLabel.ENVIRONMENT_NAME, projectName))
          .stream()
          .findFirst();
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

  public Optional<String> findFirstIdByProjectName(String projectName) {
    try {
      return dockerClient
          .listNetworks(
              ListNetworksParam.withLabel(NetworkLabel.ENVIRONMENT_NAME, projectName))
          .stream()
          .findFirst().map(Network::id);
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

  public List<Network> findByProjectName(String projectName) {
    try {
      return dockerClient
          .listNetworks(
              ListNetworksParam.withLabel(NetworkLabel.ENVIRONMENT_NAME, projectName));
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

  public Network getDefaultBridge() {
    try {
      return dockerClient
          .listNetworks(ListNetworksParam.builtInNetworks(), ListNetworksParam.withDriver("bridge"))
          .stream()
          .findFirst()
          .orElseThrow(
              () -> new IllegalStateException("Default bridge network could not be found"));
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

  public Network getCoreHabitNetwork() {
    try {
      return dockerClient
          .listNetworks(
              ListNetworksParam.withLabel(NetworkLabel.HABIT_MARKER, NetworkLabel.CORE_NETWORK))
          .stream()
          .findFirst()
          .orElseThrow(
              () -> new IllegalStateException("Core Habit network could not be found"));
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }
}
