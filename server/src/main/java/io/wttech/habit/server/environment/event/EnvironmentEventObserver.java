package io.wttech.habit.server.environment.event;

import reactor.core.publisher.Flux;

public interface EnvironmentEventObserver {

  Flux<EnvironmentEvent> getEvents();

  default Flux<EnvironmentEvent> getEvents(String environmentId) {
    return getEvents().filter(event -> event.getEnvironmentId().equals(environmentId));
  }

}
