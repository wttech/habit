package io.wttech.habit.server.request;

import io.wttech.habit.server.environment.EnvironmentApi;
import java.time.Duration;
import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;

@Component
@AllArgsConstructor
public class EnvironmentCleaningService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EnvironmentCleaningService.class);

  private final EmitterProcessor<String> processor = EmitterProcessor.create(false);
  private final FluxSink<String> sink = processor.sink();

  private final EnvironmentApi environmentApi;

  public void clean(String environmentId) {
    sink.next(environmentId);
  }

  @PostConstruct
  private void init() {
    LOGGER.info("Starting Request Cleaning Service");
    processor
        .groupBy(environmentId -> environmentId)
        .flatMap(groupedFlux -> groupedFlux.sampleTimeout(key -> Flux.interval(Duration.ofSeconds(10))))
        .subscribeOn(Schedulers.elastic())
        .subscribe(environmentApi::cleanEnvironment);
  }

}
