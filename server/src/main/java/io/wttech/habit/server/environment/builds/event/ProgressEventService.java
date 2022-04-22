package io.wttech.habit.server.environment.builds.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProgressEventService {

  private final ProgressEventRepository eventRepository;
  private final EnvironmentBuildEventPublisher eventPublisher;

  public void broadcast(ProgressEvent event) {
    log.info("Environment {}: {}", event.getEnvironmentId(), event.getMessage());
    eventRepository.add(event);
    eventPublisher.addEvent(event);
  }

}
