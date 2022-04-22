package io.wttech.habit.server.controller;

import io.wttech.habit.server.environment.Environment;
import io.wttech.habit.server.environment.EnvironmentApi;
import io.wttech.habit.server.environment.EnvironmentRepository;
import io.wttech.habit.server.environment.builds.DeployResult;
import io.wttech.habit.server.environment.builds.event.EnvironmentBuildObserver;
import io.wttech.habit.server.environment.builds.event.ProgressEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.nio.file.Path;
import java.time.Duration;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/environments")
@RequiredArgsConstructor
@Slf4j
public class EnvironmentController {

  private final EnvironmentApi api;
  private final EnvironmentRepository repository;
  private final EnvironmentBuildObserver environmentBuildObserver;

  @Operation(summary = "Retrieve all environments", description = "List of all environments including their configuration and status")
  @ApiResponse(responseCode = "200",
      description = "List of environments, may contain 0 elements",
      content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = Environment.class)
      )
  )
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Flux<Environment> getAll() {
    return Mono.fromSupplier(repository::getAll)
        .flatMapMany(list -> Flux.fromStream(list.stream()));
  }

  @Operation(summary = "Retrieve a single environment")
  @ApiResponse(responseCode = "200",
      description = "List of environments, may contain 0 elements",
      content = @Content(
          mediaType = MediaType.APPLICATION_JSON_VALUE,
          array = @ArraySchema(schema = @Schema(implementation = Environment.class))
      )
  )
  @GetMapping(value = "/{environmentId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Environment> get(@PathVariable String environmentId) {
    return Mono.fromSupplier(() -> repository.getById(environmentId));
  }

  @Operation(summary = "SSE stream of environment state changes")
  @ApiResponse(responseCode = "200", description = "SSE stream")
  @ApiResponse(responseCode = "404", description = "Environment does not exist")
  @GetMapping(value = "/{environmentId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<?>> getStateChanges(@PathVariable String environmentId) {
    Flux<ServerSentEvent<?>> events = api.getStateChanges(environmentId)
        .map(item -> ServerSentEvent.builder(item).build());
    Flux<ServerSentEvent<?>> heartbeat = Flux.interval(Duration.ofSeconds(10))
        .map(item -> ServerSentEvent.builder("").event("heartbeat").build());
    return Flux.merge(events, heartbeat);
  }

  @Operation(summary = "SSE stream of environment related events")
  @ApiResponse(responseCode = "200", description = "SSE stream")
  @ApiResponse(responseCode = "404", description = "Environment does not exist")
  @GetMapping(value = "/{environmentId}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ServerSentEvent<?>> getEvents(@PathVariable String environmentId) {
    Flux<ServerSentEvent<?>> events = api.getEvents(environmentId)
        .map(item -> ServerSentEvent.builder(item).build());
    Flux<ServerSentEvent<?>> heartbeat = Flux.interval(Duration.ofSeconds(10))
        .map(item -> ServerSentEvent.builder("").event("heartbeat").build());
    return Flux.merge(events, heartbeat);
  }

  @Operation(summary = "Delete an environment")
  @ApiResponse(responseCode = "200", description = "Environment has been successfully deleted")
  @ApiResponse(responseCode = "404", description = "Environment does not exist")
  @DeleteMapping(value = "/{environmentId}")
  public void delete(@PathVariable String environmentId) {
    api.delete(environmentId);
  }

  @Operation(summary = "Delete all environments")
  @ApiResponse(responseCode = "200", description = "All environments have been successfully deleted")
  @DeleteMapping
  public void deleteAll() {
    api.deleteAll();
  }

  @Operation(summary = "Change the state of an environment", description = "Can be used either to start or stop all elements within an environment. Stop operation does not remove the whole environment.")
  @ApiResponse(responseCode = "200", description = "Transition to the target state has been started")
  @ApiResponse(responseCode = "400", description = "Target state value not recognized")
  @PutMapping(value = "/{environmentId}/state")
  public ResponseEntity changeState(@PathVariable String environmentId, @RequestBody String state) {
    if ("up".equalsIgnoreCase(state)) {
      api.start(environmentId);
      return ResponseEntity.ok().build();
    } else if ("down".equalsIgnoreCase(state)) {
      api.shutdown(environmentId);
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.badRequest().build();
    }
  }

  @Operation(summary = "Reset the environment", description = "Executes the post-deployment logic for an environment")
  @ApiResponse(responseCode = "200", description = "Post-deployment logic executed")
  @ApiResponse(responseCode = "404", description = "Environment not found")
  @PostMapping(value = "/{environmentId}/reset")
  public void reset(@PathVariable String environmentId) {
    api.reset(environmentId);
  }

  @Operation(summary = "Deploy proxy server configuration files",
      description = "Zip file containing a particular structure is expected. Files found inside will be copied to appropriate containers and the servers themselves will be reloaded.")
  @ApiResponse(responseCode = "200", description = "Deployment successful", content = @Content(schema = @Schema(implementation = DeployResult.class)))
  @ApiResponse(responseCode = "404", description = "Environment not found")
  @PatchMapping(value = "/{environmentId}/configuration")
  public Mono<ResponseEntity<DeployResult>> deploy(@PathVariable String environmentId,
      @RequestPart(value = "file") Mono<FilePart> file) {
    return file
        .flatMap(filePart -> doDeploy(filePart, environmentId))
        .map(this::toResponseEntity);
  }

  @Operation(summary = "Update environment according to configuration and deploy proxy server configuration files",
      description = "Zip file containing a particular structure is expected. Files found inside will be copied to appropriate containers and the servers themselves will be reloaded.")
  @ApiResponse(responseCode = "200", description = "Deployment successful")
  @ApiResponse(responseCode = "404", description = "Environment not found")
  @PutMapping(value = "/{environmentId}/configuration", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<ProgressEvent> createSyncWithEventStream(@PathVariable String environmentId,
      @RequestPart(value = "file", required = false)
          Mono<FilePart> file, @RequestPart("json") String configuration) {
    return file
        .flatMap(filePart -> startEnvironmentWithDeploy(filePart, environmentId, configuration))
        .thenMany(environmentBuildObserver.getEvents(environmentId));
  }

  private Mono<Void> startEnvironmentWithDeploy(FilePart filePart, String environmentId,
      String configuration) {
    Consumer<Path> fileHandler = tempFile -> api
        .start(environmentId, configuration, tempFile);
    return FileUploadUtils.usingTemporaryFile(filePart, fileHandler);
  }

  private ResponseEntity<DeployResult> toResponseEntity(DeployResult deployResult) {
    if (deployResult.isSuccess()) {
      return ResponseEntity.ok(deployResult);
    } else {
      return ResponseEntity.unprocessableEntity().body(deployResult);
    }
  }

  private Mono<DeployResult> doDeploy(FilePart filePart, String environmentId) {
    return FileUploadUtils.flatMap(filePart, tempFile -> api.deploy(environmentId, tempFile));
  }

}
