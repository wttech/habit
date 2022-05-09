package io.wttech.habit.server.environment.requestgraph.container;

import io.wttech.habit.server.docker.container.ContainerLabel;
import io.wttech.habit.server.docker.container.ContainerType;
import io.wttech.habit.server.environment.diff.ExpectedState;
import io.wttech.habit.server.spring.ImageConfiguration;
import io.wttech.habit.server.util.EnvironmentalVariable;
import com.google.common.base.Joiner;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.HostConfig.Bind;
import com.spotify.docker.client.messages.HostConfig.Builder;
import com.spotify.docker.client.messages.Volume;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestPersisterContainerService {

  private static final String ORIGIN_DOMAIN_VARIABLE = "ORIGIN_DOMAIN";
  private static final String EXTERNAL_DOMAIN_VARIABLE = "EXTERNAL_DOMAIN";
  private static final String SSL_PORTS_VARIABLE = "SSL_PORTS";
  private static final String PORTS_VARIABLE = "PORTS";

  private final ImageConfiguration imageConfiguration;

  public ExpectedState getExpectedState(String projectName, List<Integer> sslPorts,
      List<Integer> ports, String externalDomain, String originDomain, Volume volume) {
    return ExpectedState.builder()
        .withName(generateRequestPersisterContainerName(projectName, originDomain))
        .withType("request-persister")
        .withHash(String.valueOf(
            requestPersisterContainerHashCode(sslPorts, ports, externalDomain, originDomain)))
        .withDomainNames(externalDomain)
        .withFactory(() -> createRequestPersisterContainerConfiguration(projectName,
            sslPorts, ports, externalDomain, originDomain, volume))
        .build();
  }

  private String generateRequestPersisterContainerName(String projectName, String hostname) {
    return String.format("%s_request-persister_%s", projectName, hostname);
  }

  private ContainerConfig createRequestPersisterContainerConfiguration(String projectName,
      List<Integer> sslPorts, List<Integer> ports,
      String externalDomain, String originDomain, Volume requestLogVolume) {
    Map<String, String> labels = new HashMap<>();
    labels.put(ContainerLabel.ENVIRONMENT, projectName);
    labels.put(ContainerLabel.TYPE, ContainerType.REQUEST_PERSISTER);
    labels.put(ContainerLabel.HASH, String
        .valueOf(requestPersisterContainerHashCode(sslPorts, ports, externalDomain, originDomain)));
    Builder hostConfigurationBuilder = HostConfig.builder();
    hostConfigurationBuilder.privileged(true);
    hostConfigurationBuilder.appendBinds(Bind
        .from(requestLogVolume)
        .to("/opt/request-persister/logs")
        .readOnly(false)
        .build());
    return ContainerConfig.builder()
        .image(imageConfiguration.getRequestPersisterImageId())
        .labels(labels)
        .env(EnvironmentalVariable.format(SSL_PORTS_VARIABLE, Joiner.on(" ").join(sslPorts)),
            EnvironmentalVariable.format(PORTS_VARIABLE, Joiner.on(" ").join(ports)),
            EnvironmentalVariable.format(ORIGIN_DOMAIN_VARIABLE, originDomain),
            EnvironmentalVariable.format(EXTERNAL_DOMAIN_VARIABLE, externalDomain))
        .hostConfig(hostConfigurationBuilder.build())
        .build();
  }

  private int requestPersisterContainerHashCode(List<Integer> sslPorts, List<Integer> ports,
      String externalDomain, String originDomain) {
    return Objects.hash(sslPorts, ports, externalDomain, originDomain);
  }
}
