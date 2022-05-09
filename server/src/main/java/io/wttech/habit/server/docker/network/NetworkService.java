package io.wttech.habit.server.docker.network;

import io.wttech.habit.server.docker.DockerClientException;
import io.wttech.habit.server.environment.configuration.general.NetworkConfiguration;
import com.google.common.collect.ImmutableList;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.EndpointConfig;
import com.spotify.docker.client.messages.Ipam;
import com.spotify.docker.client.messages.IpamConfig;
import com.spotify.docker.client.messages.Network;
import com.spotify.docker.client.messages.NetworkConfig;
import com.spotify.docker.client.messages.NetworkConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NetworkService {

  private final DockerClient dockerClient;

  public String createNetwork(String projectName, NetworkConfiguration networkConfiguration) {
    log.info("Creating network for subnet {}", networkConfiguration.getSubnet());
    try {
      NetworkConfig networkConfig = initialBuilder(projectName)
          .ipam(Ipam.builder()
              .driver("default")
              .config(ImmutableList.of(IpamConfig
                  .create(networkConfiguration.getSubnet(), networkConfiguration.getIpRange(),
                      networkConfiguration.getGateway()))).build())
          .build();
      return dockerClient.createNetwork(networkConfig).id();
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

  public String createNetwork(String projectName) {
    log.info("Creating network");
    try {
      return dockerClient.createNetwork(initialBuilder(projectName).build()).id();
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

  public void connectToNetwork(String networkId, String containerId, List<String> hostnames) {
    log.info("Connecting {} to network {} with aliases {}", containerId, networkId,
        String.join(", ", hostnames));
    try {
      EndpointConfig endpointConfig = EndpointConfig.builder()
          .aliases(ImmutableList.copyOf(hostnames))
          .build();
      NetworkConnection networkConnection = NetworkConnection.builder()
          .containerId(containerId)
          .endpointConfig(endpointConfig)
          .build();
      dockerClient.connectToNetwork(networkId, networkConnection);
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

  public void disconnectFromNetwork(String networkId, String containerId) {
    log.info("Disconnecting {} from network {}", containerId, networkId);
    try {
      dockerClient.disconnectFromNetwork(containerId, networkId);
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

  private NetworkConfig.Builder initialBuilder(String projectName) {
    Map<String, String> labels = new HashMap<>();
    labels.put(NetworkLabel.HABIT_MARKER, "");
    labels.put(NetworkLabel.ENVIRONMENT_NAME, projectName);
    return NetworkConfig.builder()
        .driver("bridge")
        .name(String.format("habit_environment_%s", projectName))
        .labels(labels);
  }

  public void destroy(Network network) {
    try {
      dockerClient.removeNetwork(network.id());
    } catch (DockerException | InterruptedException e) {
      throw new DockerClientException(e);
    }
  }

}
