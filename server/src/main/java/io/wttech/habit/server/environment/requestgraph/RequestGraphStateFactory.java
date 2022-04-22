package io.wttech.habit.server.environment.requestgraph;

import io.wttech.habit.server.environment.Environment;
import io.wttech.habit.server.environment.configuration.RootConfiguration;
import io.wttech.habit.server.environment.diff.ExpectedState;
import io.wttech.habit.server.environment.requestgraph.container.FrontProxyContainerService;
import io.wttech.habit.server.environment.requestgraph.container.MockServerContainerService;
import io.wttech.habit.server.environment.requestgraph.container.ProxyServerContainerService;
import io.wttech.habit.server.environment.requestgraph.container.RequestGraphServerContainerService;
import io.wttech.habit.server.environment.requestgraph.container.RequestLogService;
import io.wttech.habit.server.environment.requestgraph.container.RequestPersisterService;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RequestGraphStateFactory {

  private final RequestPersisterService requestPersisterService;
  private final RequestGraphServerContainerService requestGraphServerContainerService;
  private final MockServerContainerService mockServerContainerService;
  private final ProxyServerContainerService proxyServerContainerService;
  private final FrontProxyContainerService frontProxyContainerService;
  private final RequestLogService requestLogService;

  public List<ExpectedState> create(String environmentId,
      RootConfiguration requestConfiguration) {
    List<ExpectedState> expectedStates = new ArrayList<>();
    expectedStates
        .addAll(requestPersisterService.getExpectedState(environmentId,
            requestConfiguration));
    expectedStates.add(
        requestGraphServerContainerService.getExpectedState(environmentId,
            requestConfiguration,
            requestLogService.createVolumeIfAbsent(environmentId)));
    expectedStates.addAll(mockServerContainerService
        .getExpectedStates(environmentId,
            requestConfiguration.getMocks()));
    expectedStates
        .addAll(proxyServerContainerService.getExpectedStates(environmentId,
            requestConfiguration.getServers()));
    expectedStates.add(frontProxyContainerService.getExpectedState(environmentId));
    return expectedStates;
  }

  public List<ExpectedState> createProxyServerStates(Environment environment) {
    RootConfiguration requestConfiguration = environment.getConfiguration();
    return proxyServerContainerService.getExpectedStates(environment.getId(), requestConfiguration.getServers());
  }
}
