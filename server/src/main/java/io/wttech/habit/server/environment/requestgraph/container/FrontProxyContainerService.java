package io.wttech.habit.server.environment.requestgraph.container;

import io.wttech.habit.server.docker.container.ContainerLabel;
import io.wttech.habit.server.docker.container.ContainerType;
import io.wttech.habit.server.environment.diff.ExpectedState;
import io.wttech.habit.server.spring.ImageConfiguration;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FrontProxyContainerService {

  private final ImageConfiguration imageConfiguration;

  public ExpectedState getExpectedState(String projectName) {
    String containerName = generateContainerName(projectName);
    String hashCode = String.valueOf(imageConfiguration.getEnvironmentFrontProxyImageId().hashCode());
    return ExpectedState.builder()
        .withName(containerName)
        .withHash(hashCode)
        .withType("front-proxy")
        .withDomainNames(projectName + ".env.habit")
        .withFactory(() -> createContainerConfiguration(projectName, hashCode))
        .withExternal(true)
        .build();
  }

  private String generateContainerName(String projectName) {
    return String.format("%s_environment-front-proxy", projectName);
  }

  private ContainerConfig createContainerConfiguration(String projectName, String hash) {
    Map<String, String> labels = new HashMap<>();
    labels.put(ContainerLabel.ENVIRONMENT, projectName);
    labels.put(ContainerLabel.HASH, String.valueOf(hash));
    labels.put(ContainerLabel.TYPE, ContainerType.FRONT_PROXY);
    HostConfig.Builder hostConfigurationBuilder = HostConfig.builder()
        .capAdd("SYS_ADMIN");
    return ContainerConfig.builder()
        .image(imageConfiguration.getEnvironmentFrontProxyImageId())
        .hostConfig(hostConfigurationBuilder.build())
        .labels(labels)
        .build();
  }

}
