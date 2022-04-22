package io.wttech.habit.server.controller;

import io.wttech.habit.server.environment.EnvironmentRepository;
import io.wttech.habit.server.request.RequestApi;
import io.wttech.habit.server.request.RequestGraph;
import io.wttech.habit.server.request.RequestRepository;
import io.wttech.habit.server.request.logs.LogsApi;
import io.wttech.habit.server.request.logs.StructuredLogLine;
import io.wttech.habit.server.request.specification.RequestSpecification;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.time.Duration;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/environments/{environmentId}")
@RequiredArgsConstructor
@Slf4j
public class RequestsController {

  private final EnvironmentRepository environmentRepository;
  private final RequestRepository requestRepository;
  private final RequestApi requestApi;
  private final LogsApi logsApi;

  @Operation(summary = "Retrieve all recorded requests for a given environment", description = "")
  @ApiResponse(responseCode = "200",
      description = "List of requests, may contain 0 elements",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = RequestGraph.class))
      )
  )
  @ApiResponse(responseCode = "404", description = "Environment does not exist")
  @GetMapping(value = "/requests")
  public Flux<RequestGraph> getAllRequests(@PathVariable String environmentId, @RequestParam("count") Optional<Integer> count) {
    environmentRepository.getById(environmentId);
    if (count.isPresent()) {
      return Flux.fromIterable(requestRepository.getRecent(environmentId, count.get()));
    } else {
      return Flux.fromIterable(requestRepository.getAll(environmentId));
    }
  }

  @Operation(summary = "Issue a test request")
  @ApiResponse(responseCode = "200",
      description = "Test request data",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = RequestGraph.class)
      )
  )
  @ApiResponse(responseCode = "400", description = "Incorrect test request specification")
  @ApiResponse(responseCode = "404", description = "Environment does not exist")
  @PostMapping(value = "/requests")
  public Mono<RequestGraph> issueRequest(
      @Parameter(required = true, example = "test-environment")
      @PathVariable String environmentId,
      @Parameter(required = true)
      @RequestBody RequestSpecification requestStructure) {
    return requestApi.issueRequest(environmentId, requestStructure);
  }

  @Operation(summary = "Delete all requests and logs within a given environment")
  @ApiResponse(responseCode = "200", description = "Requests and logs have been removed")
  @ApiResponse(responseCode = "404", description = "Environment does not exist")
  @DeleteMapping(value = "/requests")
  public Mono<Void> clearRequests(@PathVariable String environmentId) {
    return Mono.fromRunnable(() -> requestApi.clearRequests(environmentId));
  }

  @Operation(summary = "Get test request data")
  @ApiResponse(responseCode = "200",
      description = "Test request data",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = RequestGraph.class)
      )
  )
  @ApiResponse(responseCode = "404", description = "Environment or request does not exist")
  @GetMapping(value = "/requests/{requestId}")
  public Mono<RequestGraph> getRequest(@PathVariable String environmentId,
      @PathVariable String requestId) {
    environmentRepository.getById(environmentId);
    return Mono.just(requestRepository.get(environmentId, requestId));
  }

  @Operation(summary = "Retrieve all logs for a given request", description = "")
  @ApiResponse(responseCode = "200",
      description = "Logs, may contain 0 elements",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = StructuredLogLine.class))
      )
  )
  @ApiResponse(responseCode = "404", description = "Environment or request does not exist")
  @GetMapping(value = "/requests/{requestId}/logs")
  public Flux<StructuredLogLine> getLogs(@PathVariable String environmentId, @PathVariable String requestId) {
    return logsApi.get(environmentId, requestId);
  }

  @Operation(summary = "Retrieve all logs for a given exchange within a request", description = "")
  @ApiResponse(responseCode = "200",
      description = "Logs, may contain 0 elements",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = StructuredLogLine.class))
      )
  )
  @ApiResponse(responseCode = "404", description = "Environment, request or exchange does not exist")
  @GetMapping(value = "/requests/{requestId}/exchanges/{exchangeId}/logs")
  public Flux<StructuredLogLine> getLogs(@PathVariable String environmentId, @PathVariable String requestId, @PathVariable String exchangeId) {
    return logsApi.get(environmentId, requestId, exchangeId);
  }

  @Operation(summary = "SSE stream of existing and newly processed logs")
  @ApiResponse(responseCode = "200", description = "SSE stream")
  @ApiResponse(responseCode = "404", description = "Environment or request does not exist")
  @GetMapping(value = "/requests/{requestId}/logstream")
  public Flux<ServerSentEvent<?>> getLogStream(@PathVariable String environmentId, @PathVariable String requestId) {
    Flux<ServerSentEvent<?>> sseStream = logsApi.getStream(environmentId, requestId)
        .map(item -> ServerSentEvent.builder(item).build());
    return sseStream.timeout(Duration.ofSeconds(10), Flux.just(ServerSentEvent.builder("").event("complete").build()));
  }

  /**
   * For internal use.
   *
   * @param environmentId environment identifier
   * @return all logs registered
   */
  @Hidden
  @GetMapping(value = "/logs")
  public Flux<StructuredLogLine> getAllLogs(@PathVariable String environmentId) {
    return Flux.fromIterable(logsApi.get(environmentId));
  }

}
