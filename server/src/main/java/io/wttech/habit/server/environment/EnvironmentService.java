package io.wttech.habit.server.environment;

import io.wttech.habit.server.HabitException;
import io.wttech.habit.server.docker.container.ContainerRepository;
import io.wttech.habit.server.docker.container.ContainerService;
import io.wttech.habit.server.docker.network.NetworkRepository;
import io.wttech.habit.server.docker.network.NetworkService;
import io.wttech.habit.server.docker.volume.VolumeService;
import io.wttech.habit.server.environment.event.EnvironmentEventBroadcaster;
import io.wttech.habit.server.environment.requestgraph.container.CommonNginxConfig;
import com.spotify.docker.client.messages.Container;
import java.time.Duration;
import java.time.Instant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnvironmentService {

  private final EnvironmentRepository environmentRepository;
  private final ContainerRepository containerRepository;
  private final NetworkRepository networkRepository;
  private final ContainerService containerService;
  private final NetworkService networkService;
  private final VolumeService volumeService;
  private final WebClient webClient;
  private final EnvironmentEventBroadcaster broadcaster;

  public void destroy(Environment environment) {
    if (environment.getState().equals(State.DOWN)) {
      log.info("Environment {} is already shut down", environment.getId());
    } else {
      log.info("Destroying environment {}", environment.getId());
      containerRepository.findByProjectName(environment.getId()).stream().map(Container::id)
          .forEach(containerService::destroy);
      networkRepository.findByProjectName(environment.getId()).forEach(networkService::destroy);
      volumeService.deleteVolumesByEnvironment(environment.getId());
      environment.setState(State.DOWN);
      environment.setLastModified(Instant.now());
      environmentRepository.add(environment);
      broadcaster.broadcastEnvironmentStateUpdate(environment);
      log.info("Destroyed environment {}", environment.getId());
    }
  }

  public Mono<Void> healthcheck(String environmentId) {
    String environmentProxyHostname = calculateEnvironmentProxyHostname(environmentId);
    return callHealthcheck(environmentProxyHostname)
        .retryWhen(Retry.backoff(5, Duration.ofMillis(100)))
        .then();
  }

  public void executePostDeployCommand(Environment environment) {
    environment.getConfiguration().getServers()
        .stream()
        .filter(
            server -> server.isDeployPresent() && server.getDeploy().isPostDeployCommandDefined())
        .forEach(server -> {
          String command = server.getDeploy().getPostDeployCommand();
          containerService
              .exec(environment.getId() + "_proxy-server_" + server.getName(), "sh", "-c", command);
          broadcaster.broadcastResetSuccess(environment.getId());
        });
  }

  private Mono<HttpStatus> callHealthcheck(String host) {
    return webClient.get()
        .uri("http://" + host + "/api/healthcheck")
        .header(HttpHeaders.HOST, CommonNginxConfig.INTERNAL_REQUEST_GRAPH_DOMAIN)
        .exchange()
        .map(ClientResponse::statusCode)
        .doOnNext(this::checkStatusCode);
  }

  private void checkStatusCode(HttpStatus status) {
    if (!status.equals(HttpStatus.OK)) {
      throw new HabitException("Environment healthcheck failed.");
    }
  }

  private String calculateEnvironmentProxyHostname(String environmentId) {
    return environmentId + ".env.habit";
  }
}
