package io.wttech.habit.server.environment;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnvironmentFactory {

  public Environment createNewEnvironmentEntity(String id) {
    Instant requestTime = Instant.now();
    return Environment.builder()
        .withId(id)
        .withConfiguration(null)
        .withBuildHash(null)
        .withCreated(requestTime)
        .withLastModified(requestTime)
        .withState(State.DOWN)
        .build();
  }

}
