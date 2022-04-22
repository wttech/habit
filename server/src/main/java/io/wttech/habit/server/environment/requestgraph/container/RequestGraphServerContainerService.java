package io.wttech.habit.server.environment.requestgraph.container;

import io.wttech.habit.server.docker.container.ContainerLabel;
import io.wttech.habit.server.docker.container.ContainerType;
import io.wttech.habit.server.environment.configuration.RootConfiguration;
import io.wttech.habit.server.environment.diff.ExpectedState;
import io.wttech.habit.server.spring.ImageConfiguration;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.HostConfig.Bind;
import com.spotify.docker.client.messages.Volume;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestGraphServerContainerService {

  private final ImageConfiguration imageConfiguration;

  public ExpectedState getExpectedState(String projectName,
      RootConfiguration requestConfiguration,
      Volume volumeIfAbsent) {
    String hashCode = String.valueOf(requestConfiguration.hashCode());
    return ExpectedState.builder()
        .withName(generateProxyTestingServerContainerName(projectName))
        .withType("request-graph-server")
        .withHash(hashCode)
        .withDomainNames(CommonNginxConfig.INTERNAL_REQUEST_GRAPH_DOMAIN)
        .withFactory(() -> createProxyTestingServerContainerConfiguration(projectName, hashCode,
            volumeIfAbsent))
        .build();
  }

  private String generateProxyTestingServerContainerName(String projectName) {
    return String.format("%s_request-graph-server", projectName);
  }

  private ContainerConfig createProxyTestingServerContainerConfiguration(
      String projectName, String hashCode, Volume requestLogVolume) {
    Map<String, String> labels = new HashMap<>();
    labels.put(ContainerLabel.ENVIRONMENT, projectName);
    labels.put(ContainerLabel.TYPE, ContainerType.REQUEST_GRAPH);
    labels.put(ContainerLabel.HASH, hashCode);
    HostConfig.Builder hostConfigurationBuilder = HostConfig.builder();
    hostConfigurationBuilder.appendBinds(Bind
        .from(requestLogVolume)
        .to("/opt/request-persister/logs")
        .readOnly(false)
        .build());
    return ContainerConfig.builder()
        .image(imageConfiguration.getRequestGraphServerImageId())
        .hostConfig(hostConfigurationBuilder.build())
        .labels(labels)
        .build();
  }
}
