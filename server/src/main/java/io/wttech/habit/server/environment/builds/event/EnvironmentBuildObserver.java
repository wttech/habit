package io.wttech.habit.server.environment.builds.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Component
@RequiredArgsConstructor
@Slf4j
public class EnvironmentBuildObserver {

  private final EmitterProcessor<ProgressEvent> processor = EmitterProcessor.create(false);
  private final FluxSink<ProgressEvent> sink = processor.sink();
  private final Flux<ProgressEvent> cachedForEach = processor.groupBy(ProgressEvent::getEnvironmentId)
      .flatMap(groupedFlux -> groupedFlux.cache(1));

  @EventListener
  public void handleEvent(ProgressEvent event) {
    sink.next(event);
  }

  public Flux<ProgressEvent> getEvents() {
    return cachedForEach;
  }

  public Flux<ProgressEvent> getEvents(String environmentId, String buildId) {
    return getEvents()
        .filter(event -> event.getEnvironmentId().equals(environmentId))
        .filter(event -> event.getBuildId().equals(buildId))
        .handle((event, filteredSink) -> {
          filteredSink.next(event);
          if (event.isCompleted() || event.isError()) {
            filteredSink.complete();
          }
        });
  }

  public Flux<ProgressEvent> getEvents(String environmentId) {
    return getEvents()
        .filter(event -> event.getEnvironmentId().equals(environmentId))
        .handle((event, filteredSink) -> {
          filteredSink.next(event);
          if (event.isCompleted() || event.isError()) {
            filteredSink.complete();
          }
        });
  }

}
