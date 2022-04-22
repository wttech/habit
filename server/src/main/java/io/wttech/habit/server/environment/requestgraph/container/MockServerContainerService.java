package io.wttech.habit.server.environment.requestgraph.container;

import io.wttech.habit.server.docker.container.ContainerLabel;
import io.wttech.habit.server.docker.container.ContainerType;
import io.wttech.habit.server.environment.configuration.request.EndpointConfiguration;
import io.wttech.habit.server.environment.diff.ExpectedState;
import io.wttech.habit.server.spring.ImageConfiguration;
import io.wttech.habit.server.util.EnvironmentalVariable;
import com.spotify.docker.client.messages.ContainerConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MockServerContainerService {

  private final ImageConfiguration imageConfiguration;

  public List<ExpectedState> getExpectedStates(String projectName,
      List<EndpointConfiguration> mockConfigurations) {
    return getAllExpectedStates(projectName, mockConfigurations);
  }

  private List<ExpectedState> getAllExpectedStates(String projectName,
      List<EndpointConfiguration> mockConfigurations) {
    List<ExpectedState> expectedStates = new ArrayList<>();
    mockConfigurations.forEach(
        endpoint -> expectedStates
            .add(getExpectedStateForEachEndpoint(projectName, endpoint.getHostname(),
                endpoint.getPorts())));
    return expectedStates;
  }

  private ExpectedState getExpectedStateForEachEndpoint(String projectName, String hostname,
      List<Integer> ports) {
    return ExpectedState.builder()
        .withName(generateMockContainerName(projectName, hostname, ports))
        .withType("mock-server")
        .withHash(String.valueOf(mockContainerHashCode(hostname, ports)))
        .withDomainNames(hostname + ".real")
        .withFactory(() -> createMockContainerConfiguration(projectName, hostname, ports))
        .build();
  }

  private String generateMockContainerName(String projectName, String hostname,
      List<Integer> ports) {
    return String
        .format("%s_mock_%s_%s", projectName, hostname, convertPortsArrayToValidString(ports));
  }

  private ContainerConfig createMockContainerConfiguration(String projectName,
      String hostname, List<Integer> ports) {
    Map<String, String> labels = new HashMap<>();
    labels.put(ContainerLabel.ENVIRONMENT, projectName);
    labels.put(ContainerLabel.TYPE, ContainerType.MOCK);
    labels.put(ContainerLabel.HOSTNAME, hostname);
    labels.put(ContainerLabel.PORT, convertPortsArrayToValidString(ports));
    labels
        .put(ContainerLabel.HASH, String.valueOf(mockContainerHashCode(hostname, ports)));
    return ContainerConfig.builder()
        .image(imageConfiguration.getMockServerImageId())
        .env(EnvironmentalVariable.format(MockServerVariable.HOSTNAME.getName(), hostname),
            EnvironmentalVariable
                .format(MockServerVariable.PORTS.getName(), StringUtils.join(ports, ' ')))
        .labels(labels)
        .build();
  }

  private String convertPortsArrayToValidString(List<Integer> ports) {
    return StringUtils.join(ports, '_');
  }

  private int mockContainerHashCode(String hostname, List<Integer> ports) {
    return Objects.hash(hostname, ports);
  }
}
