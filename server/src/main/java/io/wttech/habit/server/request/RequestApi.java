package io.wttech.habit.server.request;

import io.wttech.habit.server.environment.Environment;
import io.wttech.habit.server.environment.EnvironmentRepository;
import io.wttech.habit.server.environment.State;
import io.wttech.habit.server.request.logs.LogRepository;
import io.wttech.habit.server.request.specification.RequestSpecification;
import io.wttech.habit.server.util.JsonTranslator;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.Instant;

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class RequestApi {

  private final EnvironmentRepository environmentRepository;
  private final RequestRepository requestRepository;
  private final LogRepository logRepository;
  private final WebClient webClient;
  private final JsonTranslator jsonTranslator;
  private final EnvironmentCleaningService environmentCleaningService;
  private final EnvironmentQueue environmentQueue;

  public Mono<RequestGraph> issueRequest(String environmentId, RequestSpecification requestStructure) {
    return environmentQueue.process(environmentId, issueRequestInternal(environmentId, requestStructure));
  }

  private Mono<RequestGraph> issueRequestInternal(String environmentId, RequestSpecification requestStructure) {
    Environment environment = environmentRepository.getById(environmentId);
    checkEnvironmentStatus(environment);
    checkTargetHost(requestStructure, environment);
    Instant arrivalTimestamp = Instant.now();
    String bodyJson = jsonTranslator.asString(requestStructure);
    String environmentHost = environmentId + ".env.habit";

    return webClient.post()
        .uri("http://" + environmentHost + "/api/requests")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(bodyJson)
        .exchange()
        .flatMap(clientResponse -> clientResponse.bodyToMono(JsonNode.class))
        .map(node -> toGraph(node, arrivalTimestamp, environmentId))
        .doOnNext(this::persistGraph)
        .doOnNext(graph -> environmentCleaningService.clean(environmentId));
  }

  private void checkEnvironmentStatus(Environment environment) {
    if (!environment.getState().equals(State.UP)) {
      throw new RequestNotProcessableException("Environment is not ready. Current status: " + environment.getState().getName());
    }
  }

  public void clearRequests(String environmentId) {
    requestRepository.deleteByEnvironment(environmentId);
    logRepository.deleteByEnvironment(environmentId);
  }

  private void persistGraph(RequestGraph requestGraph) {
    requestRepository.add(requestGraph);
  }

  private RequestGraph toGraph(JsonNode rgsResponse, Instant startTimestamp, String environmentId) {
    RequestGraph.Builder builder = RequestGraph.builder();
    String requestId = rgsResponse.get("subrequests").get(0).get("request").get("rootRequestId").asText();
    builder.withId(requestId);
    builder.withEnvironmentId(environmentId);
    builder.withGraph(rgsResponse);
    builder.withStart(startTimestamp);
    builder.withEnd(Instant.now());
    return builder.build();
  }

  private void checkTargetHost(RequestSpecification request, Environment environment) {
    boolean ssl = request.getProtocol().equalsIgnoreCase("https");
    Integer port = defaultPort(request.getPort(), ssl);
    String host = request.getHost();
    boolean foundTargetMock = environment.getConfiguration().getMocks().stream()
        .filter(mock -> mock.getHostname().equals(host))
        .findAny()
        .filter(mock -> ssl ? mock.getSslPorts().contains(port) : mock.getPorts().contains(port))
        .isPresent();
    boolean foundTargetServer = environment.getConfiguration().getServers().stream()
        .filter(server -> server.getDomains().contains(host))
        .findAny()
        .filter(server -> ssl ? server.getSslPorts().contains(port) : server.getPorts().contains(port))
        .isPresent();
    boolean foundMatchingTarget = foundTargetMock || foundTargetServer;
    if (!foundMatchingTarget) {
      String portString = request.getPort() != null ? ":" + port : "";
      throw new RequestNotProcessableException("No server or mock found matching the requested target: " + request.getProtocol() + "://" + host + portString);
    }
  }

  private int defaultPort(Integer port, boolean ssl) {
    if (port != null) {
      return port;
    }
    if (ssl) {
      return 443;
    }
    return 80;
  }
}
