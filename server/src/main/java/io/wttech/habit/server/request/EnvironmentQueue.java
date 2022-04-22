package io.wttech.habit.server.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@AllArgsConstructor
public class EnvironmentQueue {

  private final EmitterProcessor<OperationWrapper<?>> processor = EmitterProcessor.create(false);
  private final FluxSink<OperationWrapper<?>> sink = processor.sink();
  private final Flux<? extends OperationWrapper<?>> processedGrouped = processor
      .groupBy(OperationWrapper::getEnvironmentId)
      .parallel()
      .runOn(Schedulers.boundedElastic())
      .flatMap(group -> group.concatMap(item -> item.getOperation().map(item::withResult)))
      .sequential()
      .publish()
      .autoConnect(0);

  public <T> Mono<T> process(String environmentId, Mono<T> operation) {
    OperationWrapper<T> toProcess = OperationWrapper.withoutResult(environmentId, operation);
    Mono<T> listener = processedGrouped
        .filter(processed -> processed.getOperation() == operation)
        .next()
        .map(processed -> (T) processed.getResult());
    Mono<T> producer = Mono.fromRunnable(() -> sink.next(toProcess));
    return listener.mergeWith(producer).next();

  }

  @AllArgsConstructor
  @Getter
  private static class OperationWrapper<T> {

    private final String environmentId;
    private final Mono<T> operation;
    private final Object result;

    public static <T> OperationWrapper<T> withoutResult(String environmentId, Mono<T> operation) {
      return new OperationWrapper<>(environmentId, operation, null);
    }

    public OperationWrapper<T> withResult(Object result) {
      return new OperationWrapper<>(environmentId, operation, result);
    }

  }

}
