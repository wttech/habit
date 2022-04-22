package io.wttech.habit.server.environment.requestgraph.container;

import io.wttech.habit.server.environment.configuration.RootConfiguration;
import io.wttech.habit.server.environment.configuration.request.EndpointConfiguration;
import io.wttech.habit.server.environment.diff.ExpectedState;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestPersisterService {

  private final RequestPersisterContainerService requestPersisterContainerService;
  private final RequestLogService requestLogService;

  public List<ExpectedState> getExpectedState(String projectName,
      RootConfiguration requestConfiguration) {
    List<Ports> ports = retrievePortConfigurations(requestConfiguration);
    List<ExpectedState> expectedStates = new ArrayList<>();
    ports.forEach(port -> expectedStates.add(requestPersisterContainerService
        .getExpectedState(projectName, port.getSsl(), port.getNormal(),
            port.getHostname(), port.getHostname() + ".real",
            requestLogService.createVolumeIfAbsent(projectName))));
    return expectedStates;
  }

  private List<Ports> retrievePortConfigurations(RootConfiguration requestConfiguration) {
    List<Ports> result = new ArrayList<>();
    Ports externalDomainTemplate = Ports.empty("");
    Set<Integer> ports = requestConfiguration.getServers().stream()
        .flatMap(server -> server.getPorts().stream()).collect(Collectors.toSet());
    Set<Integer> sslPorts = requestConfiguration.getServers().stream()
        .flatMap(server -> server.getSslPorts().stream()).collect(Collectors.toSet());
    ports.forEach(externalDomainTemplate::addPort);
    sslPorts.forEach(externalDomainTemplate::addSslPort);
    requestConfiguration.getServers().stream()
        .flatMap(server -> server.getDomains().stream())
        .map(externalDomainTemplate::withHostname)
        .forEach(result::add);
    requestConfiguration.getMocks().forEach(
        endpointConfiguration -> result.add(setupPortsFromConfiguration(endpointConfiguration)));
    return result;
  }

  private static Ports setupPortsFromConfiguration(EndpointConfiguration endpointConfiguration) {
    Ports newPort = new Ports(endpointConfiguration.getHostname());
    newPort.addSslPorts((endpointConfiguration.getSslPorts()));
    newPort.addPorts(endpointConfiguration.getPorts());
    return newPort;
  }

  private static class Ports {

    private String hostname;
    private List<Integer> normal = new ArrayList<>();
    private List<Integer> ssl = new ArrayList<>();

    public Ports(String hostname) {
      this.hostname = hostname;
    }

    public static Ports empty(String hostname) {
      return new Ports(hostname);
    }

    public Ports withHostname(String hostname) {
      Ports ports = new Ports(hostname);
      ports.addPorts(getNormal());
      ports.addSslPorts(getSsl());
      return ports;
    }

    public String getHostname() {
      return hostname;
    }

    public void addPort(int port) {
      normal.add(port);
    }

    public void addPorts(Collection<Integer> ports) {
      normal.addAll(ports);
    }

    public void addSslPort(int port) {
      ssl.add(port);
    }

    public void addSslPorts(Collection<Integer> ports) {
      ssl.addAll(ports);
    }

    public List<Integer> getNormal() {
      return normal;
    }

    public List<Integer> getSsl() {
      return ssl;
    }
  }
}
