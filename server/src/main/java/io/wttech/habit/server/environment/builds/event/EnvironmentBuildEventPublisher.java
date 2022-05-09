package io.wttech.habit.server.environment.builds.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnvironmentBuildEventPublisher {

  private final ApplicationEventPublisher eventPublisher;

  public void addEvent(ProgressEvent event) {
    eventPublisher.publishEvent(event);
  }

}
