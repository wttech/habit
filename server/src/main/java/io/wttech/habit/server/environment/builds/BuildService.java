package io.wttech.habit.server.environment.builds;

import io.wttech.habit.server.docker.container.ContainerRepository;
import io.wttech.habit.server.docker.network.NetworkRepository;
import io.wttech.habit.server.docker.network.NetworkService;
import io.wttech.habit.server.environment.BuildHashProvider;
import io.wttech.habit.server.environment.Environment;
import io.wttech.habit.server.environment.EnvironmentRepository;
import io.wttech.habit.server.environment.EnvironmentService;
import io.wttech.habit.server.environment.State;
import io.wttech.habit.server.environment.builds.event.ProgressEvent;
import io.wttech.habit.server.environment.builds.event.ProgressEventService;
import io.wttech.habit.server.environment.configuration.RootConfiguration;
import io.wttech.habit.server.environment.diff.BuildProcessManager;
import io.wttech.habit.server.environment.diff.ContainerState;
import io.wttech.habit.server.environment.diff.EnvironmentBuildProcess;
import io.wttech.habit.server.environment.diff.ExpectedState;
import io.wttech.habit.server.environment.diff.StateComparingService;
import io.wttech.habit.server.environment.diff.step.BuildStep;
import io.wttech.habit.server.environment.event.EnvironmentEventBroadcaster;
import io.wttech.habit.server.environment.requestgraph.RequestGraphStateFactory;
import io.wttech.habit.server.util.TarService;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildService {

  private final EnvironmentService environmentService;
  private final NetworkService networkService;
  private final StateComparingService stateComparingService;
  private final NetworkRepository networkRepository;
  private final ContainerRepository containerRepository;
  private final BuildHashProvider buildHashProvider;
  private final TarService tarService;
  private final EnvironmentRepository environmentRepository;
  private final RequestGraphStateFactory requestGraphStateFactory;
  private final BuildProcessManager buildProcessManager;
  private final ProgressEventService progressEventService;
  private final EnvironmentEventBroadcaster environmentEventBroadcaster;

  public void start(Environment environment, RootConfiguration rootConfiguration, Path tarPath) {
    log.info("Start of environment creation / update");
    String buildHash = buildHashProvider.getBuildHash();

    handleRecreation(environment, rootConfiguration);
    updateEnvironmentEntity(environment, buildHash, rootConfiguration);

    log.info("Handling configuration tar");
    Path configurationToDeploy = extractDeployment(tarPath);

    executeBuildProcess(environment, configurationToDeploy);
  }

  public void start(Environment environment) {
    log.info("Start of environment creation / update");
    String buildHash = buildHashProvider.getBuildHash();

    updateEnvironmentEntity(environment, buildHash, environment.getConfiguration());
    executeBuildProcess(environment);

  }

  public Mono<DeployResult> deploy(Environment environment, Path tarPath) {
    log.info("Performing deploy only");
    Path configurationToDeploy = extractDeployment(tarPath);

    String environmentNetwork = getEnvironmentNetwork(environment.getId(),
        environment.getConfiguration());

    List<ExpectedState> proxyServerStates = requestGraphStateFactory
        .createProxyServerStates(environment);

    List<BuildStep> steps = proxyServerStates.stream()
        .flatMap(state -> stateComparingService
            .calculateStepsForDeployOnly(state, environmentNetwork, configurationToDeploy).stream())
        .collect(Collectors.toList());

    return Mono.fromRunnable(() -> environmentEventBroadcaster.broadcastDeployStart(environment.getId()))
        .thenMany(Flux.fromIterable(steps))
        .flatMap(BuildStep::perform)
        .doFinally(signal -> cleanupDeployment(configurationToDeploy))
        .then(Mono.fromSupplier(DeployResult::success))
        .doOnNext(result -> environmentEventBroadcaster.broadcastDeploySuccess(environment.getId()))
        .doOnError(e -> environmentEventBroadcaster.broadcastDeployError(environment.getId(), e.getMessage()))
        .onErrorResume(e -> Mono.just(DeployResult.error(e.getMessage())));
  }

  private void setEnvironmentUpStatus(Environment environment) {
    environment.setState(State.UP);
    environment.setLastModified(Instant.now());
    environmentRepository.add(environment);
    environmentEventBroadcaster.broadcastEnvironmentStateUpdate(environment);
  }

  private void setEnvironmentErrorStatus(Environment environment) {
    environment.setState(State.ERROR);
    environment.setLastModified(Instant.now());
    environmentRepository.add(environment);
    environmentEventBroadcaster.broadcastEnvironmentStateUpdate(environment);
  }

  private void updateEnvironmentEntity(Environment environment, String buildHash,
      RootConfiguration rootConfiguration) {
    environment.setLastModified(Instant.now());
    environment.setBuildHash(buildHash);
    environment.setConfiguration(rootConfiguration);
    environment.setState(State.STARTING);
    environmentRepository.add(environment);
    environmentEventBroadcaster.broadcastEnvironmentStateUpdate(environment);
  }

  private void cleanupDeployment(Path configurationToDeploy) {
    try {
      FileUtils.deleteDirectory(configurationToDeploy.toFile());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private void handleRecreation(Environment env,
      RootConfiguration rootConfiguration) {
    if (shouldBeDestroyed(env, rootConfiguration)) {
      environmentService.destroy(env);
    }
  }

  private boolean shouldBeDestroyed(Environment env,
      RootConfiguration rootConfiguration) {
    return environmentNetworkDiffers(env, rootConfiguration);
  }

  private boolean environmentNetworkDiffers(Environment environment,
      RootConfiguration rootConfiguration) {
    return environment.isConfigurationPresent() && !environment.getConfiguration().getNetwork()
        .equals(rootConfiguration.getNetwork());
  }

  private void executeBuildProcess(Environment environment, Path configurationToDeploy) {
    RootConfiguration rootConfiguration = environment.getConfiguration();
    List<ExpectedState> expectedStates = requestGraphStateFactory
        .create(environment.getId(), rootConfiguration);

    String environmentNetwork = getEnvironmentNetwork(environment.getId(),
        rootConfiguration);

    List<ContainerState> containerStates = getCurrentState(environment.getId());
    EnvironmentBuildProcess buildProcess = stateComparingService
        .calculateRequiredActions(environment.getId(), containerStates, expectedStates,
            environmentNetwork, configurationToDeploy);

    environmentEventBroadcaster.broadcastBuildStart(environment.getId());

    broadcastBuildProcessStartEvent(buildProcess);

    buildProcessManager.perform(buildProcess)
        .doOnError(e -> setEnvironmentErrorStatus(environment))
        .then(Mono.fromRunnable(() -> setEnvironmentUpStatus(environment)))
        .doFinally(signal -> cleanupDeployment(configurationToDeploy))
        .subscribeOn(Schedulers.elastic())
        .subscribe();
  }

  private void executeBuildProcess(Environment environment) {
    RootConfiguration rootConfiguration = environment.getConfiguration();
    List<ExpectedState> expectedStates = requestGraphStateFactory
        .create(environment.getId(), rootConfiguration);

    String environmentNetwork = getEnvironmentNetwork(environment.getId(),
        rootConfiguration);

    List<ContainerState> containerStates = getCurrentState(environment.getId());
    EnvironmentBuildProcess buildProcess = stateComparingService
        .calculateActionsWithoutDeploy(environment, containerStates, expectedStates,
            environmentNetwork);

    environmentEventBroadcaster.broadcastBuildStart(environment.getId());
    broadcastBuildProcessStartEvent(buildProcess);

    buildProcessManager.perform(buildProcess)
        .doOnError(e -> setEnvironmentErrorStatus(environment))
        .then(Mono.fromRunnable(() -> setEnvironmentUpStatus(environment)))
        .subscribeOn(Schedulers.elastic())
        .subscribe();
  }

  private void broadcastBuildProcessStartEvent(EnvironmentBuildProcess buildProcess) {
    ProgressEvent startEvent = ProgressEvent.message()
        .withEnvironmentId(buildProcess.getEnvironmentId())
        .withBuildId(buildProcess.getBuildId().toString())
        .withMessage("Build start")
        .build();

    progressEventService.broadcast(startEvent);
  }

  private Path extractDeployment(Path tarPath) {
    Path configurationToDeploy;
    try (InputStream inputStream = Files.newInputStream(tarPath)) {
      configurationToDeploy = Files.createTempDirectory("habit-deploy");
      tarService.unTar(inputStream, configurationToDeploy);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return configurationToDeploy;
  }

  private String getEnvironmentNetwork(String key,
      RootConfiguration environmentConfiguration) {
    return networkRepository
        .findFirstIdByProjectName(key)
        .orElseGet(() -> createNetwork(key, environmentConfiguration));
  }

  private String createNetwork(String key, RootConfiguration environmentConfiguration) {
    return environmentConfiguration.getNetwork()
        .map(networkConfiguration -> networkService.createNetwork(key, networkConfiguration))
        .orElseGet(() -> networkService.createNetwork(key));
  }

  private List<ContainerState> getCurrentState(String projectName) {
    return containerRepository.getAllContainers(projectName)
        .stream()
        .map(ContainerState::new)
        .collect(Collectors.toList());
  }

}
