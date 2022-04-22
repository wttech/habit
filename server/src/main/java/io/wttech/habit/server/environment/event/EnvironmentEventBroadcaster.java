package io.wttech.habit.server.environment.event;

import io.wttech.habit.server.environment.Environment;
import io.wttech.habit.server.event.EventCenter;
import java.time.Instant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnvironmentEventBroadcaster {

  private final EnvironmentEventPublisher publisher;
  private final EventCenter eventCenter;

  public void broadcastEnvironmentStateUpdate(Environment environment) {
    EnvironmentUpdateEvent event = EnvironmentUpdateEvent.builder()
        .withEnvironment(environment)
        .withTimestamp(Instant.now())
        .build();
    eventCenter.publishEvent(event);
  }

  public void broadcastDeployStart(String environmentId) {
    BasicEnvironmentEvent event = BasicEnvironmentEvent.builder()
        .withEnvironmentId(environmentId)
        .withTimestamp(Instant.now())
        .withType("DEPLOY_START")
        .withMessage("Deploy has been started")
        .build();
    publisher.publishEvent(event);
  }

  public void broadcastDeploySuccess(String environmentId) {
    BasicEnvironmentEvent event = BasicEnvironmentEvent.builder()
        .withEnvironmentId(environmentId)
        .withTimestamp(Instant.now())
        .withType("DEPLOY_SUCCESS")
        .withMessage("Deploy has been successful")
        .build();
    publisher.publishEvent(event);
  }

  public void broadcastDeployError(String environmentId, String message) {
    BasicEnvironmentEvent event = BasicEnvironmentEvent.builder()
        .withEnvironmentId(environmentId)
        .withTimestamp(Instant.now())
        .withType("DEPLOY_ERROR")
        .withMessage("Error occurred during deploy.\n" + message)
        .build();
    publisher.publishEvent(event);
  }

  public void broadcastBuildStart(String environmentId) {
    BasicEnvironmentEvent event = BasicEnvironmentEvent.builder()
        .withEnvironmentId(environmentId)
        .withTimestamp(Instant.now())
        .withType("START")
        .withMessage("Environment build has been started")
        .build();
    publisher.publishEvent(event);
  }

  public void broadcastBuildEnd(String environmentId) {
    BasicEnvironmentEvent event = BasicEnvironmentEvent.builder()
        .withEnvironmentId(environmentId)
        .withTimestamp(Instant.now())
        .withType("END")
        .withMessage("Environment build has been completed successfully")
        .build();
    publisher.publishEvent(event);
  }

  public void broadcastResetSuccess(String environmentId) {
    BasicEnvironmentEvent event = BasicEnvironmentEvent.builder()
        .withEnvironmentId(environmentId)
        .withTimestamp(Instant.now())
        .withType("RESET_SUCCESS")
        .withMessage("Environment has been reset")
        .build();
    publisher.publishEvent(event);
  }
}
