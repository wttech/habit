package io.wttech.habit.server.environment;

import io.wttech.habit.server.environment.builds.BuildService;
import io.wttech.habit.server.environment.builds.DeployResult;
import io.wttech.habit.server.environment.configuration.ConfigurationParser;
import io.wttech.habit.server.environment.configuration.RootConfiguration;
import io.wttech.habit.server.environment.event.EnvironmentEvent;
import io.wttech.habit.server.environment.event.EnvironmentEventObserver;
import io.wttech.habit.server.environment.event.EnvironmentUpdateEvent;
import io.wttech.habit.server.event.EventCenter;
import io.wttech.habit.server.request.RequestRepository;
import io.wttech.habit.server.request.logs.LogsApi;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnvironmentApi {

  private static final Duration REQUEST_TTL = Duration.ofDays(1);

  private final EnvironmentService environmentService;
  private final EnvironmentRepository environmentRepository;
  private final EnvironmentEventObserver environmentEventObserver;
  private final RequestRepository requestRepository;
  private final LogsApi logsApi;
  private final BuildHashProvider buildHashProvider;
  private final BuildService buildService;
  private final ConfigurationParser parser;
  private final EnvironmentFactory environmentFactory;
  private final EventCenter eventCenter;

  public Flux<EnvironmentEvent> getEvents(String environmentId) {
    return environmentEventObserver.getEvents(environmentId);
  }

  public Flux<EnvironmentUpdateEvent> getStateChanges(String environmentId) {
    return eventCenter.getEvents(EnvironmentUpdateEvent.class)
        .filter(event -> event.getEnvironmentId().equals(environmentId));
  }

  public void start(String id, String configurationsJson, Path tarPath) {
    log.info("Starting environment {} with deployment", id);
    RootConfiguration rootConfiguration = parser
        .parse(configurationsJson);
    Environment environment = environmentRepository.findById(id)
        .orElseGet(() -> environmentFactory.createNewEnvironmentEntity(id));
    environmentRepository.add(environment);
    buildService.start(environment, rootConfiguration, tarPath);
  }

  public void start(String environmentId) {
    log.info("Starting environment {} without deployment", environmentId);
    Environment environment = environmentRepository.getById(environmentId);
    buildService.start(environment);
  }

  public void startAllUp() {
    log.info("Starting all eligible environments");
    environmentRepository.findAllUp()
        .forEach(buildService::start);
  }

  public Mono<DeployResult> deploy(String environmentId, Path tarPath) {
    log.info("Deploying to environment {}", environmentId);
    Environment environment = environmentRepository.getById(environmentId);
    return buildService.deploy(environment, tarPath);
  }

  public void shutdown(String id) {
    log.info("Shutting down environment {}", id);
    Environment environment = environmentRepository.getById(id);
    environmentService.destroy(environment);
  }

  public void shutdownAll() {
    log.info("Shutting down all environments");
    environmentRepository.getAll()
        .forEach(environmentService::destroy);
  }

  public void delete(String id) {
    log.info("Deleting environment {}", id);
    Environment environment = environmentRepository.getById(id);
    environmentService.destroy(environment);
    environmentRepository.removeById(id);
  }

  public void deleteAll() {
    log.info("Deleting all environments");
    environmentRepository.getAll()
        .forEach(environment -> {
          environmentService.destroy(environment);
          environmentRepository.removeById(environment.getId());
        });
  }

  public void destroyAllOfPreviousVersion() {
    log.info("Destroying all environments created with old version");
    String buildHash = buildHashProvider.getBuildHash();
    environmentRepository.getAll().stream()
        .filter(environment -> !environment.getState().equals(State.DOWN))
        .filter(environment -> !buildHash.equals(environment.getBuildHash()))
        .forEach(environmentService::destroy);
  }

  public void cleanEnvironment(String environmentId) {
    Environment environment = environmentRepository.getById(environmentId);
    Instant threshold = Instant.now().minus(REQUEST_TTL);
    requestRepository.deleteOlderThan(environmentId, threshold);
    logsApi.cleanOldLogs(environmentId, threshold);
  }

  public void reset(String environmentId) {
    Environment environment = environmentRepository.getById(environmentId);
    environmentService.executePostDeployCommand(environment);
  }
}
