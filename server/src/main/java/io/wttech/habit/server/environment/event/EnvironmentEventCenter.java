package io.wttech.habit.server.environment.event;

import io.wttech.habit.server.event.EventCenter;
import javax.inject.Inject;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class EnvironmentEventCenter implements EnvironmentEventPublisher, EnvironmentEventObserver {

  private final EventCenter eventCenter;
  private final Flux<Flux<EnvironmentEvent>> cached;

  @Inject
  EnvironmentEventCenter(EventCenter eventCenter) {
    this.eventCenter = eventCenter;
    cached = eventCenter.getEvents(EnvironmentEvent.class).groupBy(EnvironmentEvent::getEnvironmentId)
        .map(groupedFlux -> groupedFlux.cache(1))
        .replay().autoConnect(0);
  }

  @Override
  public Flux<EnvironmentEvent> getEvents() {
    return cached.flatMap(groupedFlux -> groupedFlux);
  }

  @Override
  public void publishEvent(EnvironmentEvent event) {
    eventCenter.publishEvent(event);
  }

}
