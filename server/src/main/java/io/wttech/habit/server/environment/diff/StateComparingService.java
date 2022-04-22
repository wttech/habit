package io.wttech.habit.server.environment.diff;

import io.wttech.habit.server.docker.network.NetworkRepository;
import io.wttech.habit.server.environment.Environment;
import io.wttech.habit.server.environment.diff.deploy.ProxyServerDeployment;
import io.wttech.habit.server.environment.diff.step.BuildStep;
import io.wttech.habit.server.environment.diff.step.BuildStepFactory;
import com.spotify.docker.client.messages.Network;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StateComparingService {

  private final NetworkRepository networkRepository;
  private final BuildStepFactory buildStepFactory;

  public EnvironmentBuildProcess calculateRequiredActions(String environmentId,
      List<ContainerState> containerStates,
      List<ExpectedState> expectedStates, String environmentNetwork, Path deployedConfiguration) {
    List<BuildStep> steps = new ArrayList<>();
    LinkedHashMap<ExpectedState, ContainerState> matchingStates = getMatchingStates(containerStates,
        expectedStates);
    steps.addAll(destroyUnusedContainers(matchingStates, containerStates));
    matchingStates.forEach((expectedState, containerState) ->
        steps
            .addAll(compareStates(expectedState, containerState, environmentNetwork,
                deployedConfiguration)));
    steps.add(buildStepFactory.waiting(Duration.ofSeconds(2)));
    steps.add(buildStepFactory.healthcheck(environmentId));
    steps.addAll(healthchecks(expectedStates));
    steps.addAll(logTailing(environmentId, expectedStates));
    return EnvironmentBuildProcess.of(environmentId, steps);
  }

  public List<BuildStep> calculateStepsForDeployOnly(ExpectedState expectedState, String environmentNetwork, Path deployedConfiguration) {
    List<BuildStep> buildSteps;
    if (expectedState.getProxyServerDeployment().isReloadCommandDefined()) {
      buildSteps = reloadableDeploy(expectedState, deployedConfiguration);
    } else {
      buildSteps = recreateContainer(expectedState, environmentNetwork,
          deployedConfiguration);
    }
    return buildSteps;
  }

  public EnvironmentBuildProcess calculateActionsWithoutDeploy(Environment environment,
      List<ContainerState> containerStates,
      List<ExpectedState> expectedStates, String environmentNetwork) {
    List<BuildStep> steps = new ArrayList<>();
    LinkedHashMap<ExpectedState, ContainerState> matchingStates = getMatchingStates(containerStates,
        expectedStates);
    steps.addAll(destroyUnusedContainers(matchingStates, containerStates));
    matchingStates.forEach((expectedState, containerState) ->
        steps
            .addAll(compareStates(expectedState, containerState, environmentNetwork)));
    steps.add(buildStepFactory.waiting(Duration.ofSeconds(2)));
    steps.add(buildStepFactory.healthcheck(environment.getId()));
    steps.addAll(healthchecks(expectedStates));
    steps.addAll(logTailing(environment.getId(), expectedStates));
    return EnvironmentBuildProcess.of(environment.getId(), steps);
  }

  private List<BuildStep> healthchecks(List<ExpectedState> expectedStates) {
    return expectedStates.stream().filter(state -> "proxy-server".equals(state.getType()))
        .map(state -> buildStepFactory.proxyServerHealthcheck(state.getContainerName()))
        .collect(Collectors.toList());
  }

  private List<BuildStep> logTailing(String environmentId, List<ExpectedState> expectedStates) {
    return expectedStates.stream()
        .filter(state -> !state.getLogFiles().isEmpty())
        .map(state -> buildStepFactory
            .tail(environmentId, "", state.getContainerName(), state.getLogFiles()))
        .collect(Collectors.toList());
  }

  private LinkedHashMap<ExpectedState, ContainerState> getMatchingStates(
      List<ContainerState> containerStates, List<ExpectedState> expectedStates) {
    LinkedHashMap<ExpectedState, ContainerState> matchingStates = new LinkedHashMap<>();
    expectedStates
        .forEach(expectedState -> matchingStates.put(expectedState, containerStates.stream()
            .filter(
                containerState -> containerState.getName().equals(expectedState.getContainerName()))
            .findAny().orElse(null))
        );
    return matchingStates;
  }

  private List<BuildStep> compareStates(ExpectedState expectedState,
      ContainerState containerState,
      String environmentNetwork, Path deployedConfiguration) {
    List<BuildStep> buildSteps = new ArrayList<>();
    if (containerState == null) {
      buildSteps
          .addAll(createNewContainer(expectedState, environmentNetwork, deployedConfiguration));
    } else {
      buildSteps
          .addAll(
              calculateActionForExistingContainer(expectedState, containerState, environmentNetwork,
                  deployedConfiguration));
    }
    return buildSteps;
  }

  private List<BuildStep> compareStates(ExpectedState expectedState,
      ContainerState containerState,
      String environmentNetwork) {
    List<BuildStep> buildSteps = new ArrayList<>();
    if (containerState == null) {
      buildSteps
          .addAll(createNewContainer(expectedState, environmentNetwork));
    } else {
      buildSteps
          .addAll(
              calculateActionForExistingContainer(expectedState, containerState, environmentNetwork));
    }
    return buildSteps;
  }

  private List<BuildStep> calculateActionForExistingContainer(ExpectedState expectedState,
      ContainerState containerState, String environmentNetwork, Path deployedConfiguration) {
    List<BuildStep> buildSteps;
    if (expectedState.getHash().equals(containerState.getHash())) {
      buildSteps = stepsForSameHash(expectedState, containerState, environmentNetwork, deployedConfiguration);
    } else {
      buildSteps = recreateContainer(expectedState, environmentNetwork,
          deployedConfiguration);
    }
    return buildSteps;
  }

  private List<BuildStep> calculateActionForExistingContainer(ExpectedState expectedState,
      ContainerState containerState, String environmentNetwork) {
    List<BuildStep> buildSteps = new ArrayList<>();
    if (expectedState.getHash().equals(containerState.getHash())) {
      if (!containerState.isRunning()) {
        buildSteps.add(buildStepFactory.start(containerState.getId()));
      }
    } else {
      buildSteps = recreateContainer(expectedState, environmentNetwork);
    }
    return buildSteps;
  }

  private List<BuildStep> stepsForSameHash(ExpectedState expectedState, ContainerState containerState, String environmentNetwork, Path deployedConfiguration) {
    List<BuildStep> buildSteps;
    if (!containerState.isRunning()) {
      buildSteps = startableDeploy(expectedState, containerState, deployedConfiguration);
    } else if (expectedState.isDeployRequired(containerState)) {
      if (expectedState.getProxyServerDeployment().isReloadCommandDefined()) {
        buildSteps = reloadableDeploy(expectedState, deployedConfiguration);
      } else {
        buildSteps = recreateContainer(expectedState, environmentNetwork,
            deployedConfiguration);
      }
    } else {
      buildSteps = new ArrayList<>();
    }
    return buildSteps;
  }

  private List<BuildStep> startableDeploy(ExpectedState expectedState,
      ContainerState containerState, Path deployedConfiguration) {
    List<BuildStep> steps = new ArrayList<>();
    if (expectedState.isDeployRequired(containerState)) {
      steps.add(buildStepFactory
          .deploy(expectedState.getContainerName(), expectedState.getProxyServerDeployment(),
              deployedConfiguration));
    }
    steps.add(buildStepFactory.start(containerState.getId()));
    if (expectedState.isDeploymentPresent()) {
      ProxyServerDeployment proxyServerDeployment = expectedState.getProxyServerDeployment();
      if (proxyServerDeployment.isPostDeployCommandDefined()) {
        steps.add(buildStepFactory.command(expectedState.getContainerName(),
            proxyServerDeployment.getPostDeployCommand()));
      }
    }
    return steps;
  }

  private List<BuildStep> reloadableDeploy(ExpectedState expectedState,
      Path deployedConfiguration) {
    List<BuildStep> steps = new ArrayList<>();
    steps.add(buildStepFactory
        .deploy(expectedState.getContainerName(), expectedState.getProxyServerDeployment(),
            deployedConfiguration));
    steps.add(buildStepFactory.command(expectedState.getContainerName(),
        expectedState.getProxyServerDeployment().getReloadCommand()));
    ProxyServerDeployment proxyServerDeployment = expectedState.getProxyServerDeployment();
    if (proxyServerDeployment.isPostDeployCommandDefined()) {
      steps.add(buildStepFactory
          .command(expectedState.getContainerName(), proxyServerDeployment.getPostDeployCommand()));
    }
    return steps;
  }

  private List<BuildStep> recreateContainer(ExpectedState expectedState, String environmentNetwork, Path deployedConfiguration) {
    List<BuildStep> steps = new ArrayList<>();
    steps.add(buildStepFactory.destroy(expectedState.getContainerName()));
    steps.addAll(createNewContainer(expectedState, environmentNetwork, deployedConfiguration));
    return steps;
  }

  private List<BuildStep> recreateContainer(ExpectedState expectedState, String environmentNetwork) {
    List<BuildStep> steps = new ArrayList<>();
    steps.add(buildStepFactory.destroy(expectedState.getContainerName()));
    steps.addAll(createNewContainer(expectedState, environmentNetwork));
    return steps;
  }

  private List<BuildStep> createNewContainer(ExpectedState expectedState, String environmentNetwork,
      Path deployedConfiguration) {
    List<BuildStep> steps = new ArrayList<>();
    steps.add(buildStepFactory
        .create(expectedState.getFactory().get(), expectedState.getContainerName()));
    steps.addAll(networkManagement(expectedState, environmentNetwork));
    steps.addAll(deployment(expectedState, deployedConfiguration));
    steps.add(buildStepFactory.start(expectedState.getContainerName()));
    return steps;
  }

  private List<BuildStep> createNewContainer(ExpectedState expectedState, String environmentNetwork) {
    List<BuildStep> steps = new ArrayList<>();
    steps.add(buildStepFactory
        .create(expectedState.getFactory().get(), expectedState.getContainerName()));
    steps.addAll(networkManagement(expectedState, environmentNetwork));
    steps.add(buildStepFactory.start(expectedState.getContainerName()));
    return steps;
  }

  private List<BuildStep> deployment(ExpectedState expectedState,
      Path deployedConfiguration) {
    List<BuildStep> steps = new ArrayList<>();
    if (expectedState.isDeploymentPresent()) {
      ProxyServerDeployment proxyServerDeployment = expectedState.getProxyServerDeployment();
      steps.add(buildStepFactory
          .deploy(expectedState.getContainerName(), proxyServerDeployment, deployedConfiguration));
      if (proxyServerDeployment.isPostDeployCommandDefined()) {
        steps.add(buildStepFactory.command(expectedState.getContainerName(),
            proxyServerDeployment.getPostDeployCommand()));
      }
    }
    return steps;
  }

  private List<BuildStep> networkManagement(ExpectedState expectedState, String environmentNetwork) {
    List<BuildStep> steps = new ArrayList<>();
    steps.add(buildStepFactory.disconnectFromBridge(expectedState.getContainerName()));
    if (expectedState.isExternal()) {
      Network coreHabitNetwork = networkRepository.getCoreHabitNetwork();
      steps
          .add(buildStepFactory.connect(expectedState.getContainerName(), coreHabitNetwork.id(),
              expectedState.getDomainNames()));
    }
    steps
        .add(buildStepFactory.connect(expectedState.getContainerName(), environmentNetwork,
            expectedState.getDomainNames()));
    return steps;
  }

  private List<BuildStep> destroyUnusedContainers(
      LinkedHashMap<ExpectedState, ContainerState> matchingStates,
      List<ContainerState> containerStates) {
    return containerStates.stream()
        .filter(existingContainer -> !matchingStates.values().contains(existingContainer))
        .map(existingContainer -> buildStepFactory.destroy(existingContainer.getId()))
        .collect(Collectors.toList());
  }

}
