package io.wttech.habit.server.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Component
@RequiredArgsConstructor
public class EventCenter {

  private final EmitterProcessor<Object> processor = EmitterProcessor.create(false);
  private final FluxSink<Object> sink = processor.sink();
  private final Flux<Object> external = processor
      .publish().autoConnect(0);

  public Flux<Object> getEvents() {
    return external;
  }

  public <T> Flux<T> getEvents(Class<T> clazz) {
    return external.filter(clazz::isInstance).cast(clazz);
  }

  public void publishEvent(Object event) {
    sink.next(event);
  }

}
