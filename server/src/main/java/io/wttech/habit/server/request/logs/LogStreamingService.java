package io.wttech.habit.server.request.logs;

import io.wttech.habit.server.request.RequestGraph;
import org.springframework.stereotype.Component;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

@Component
public class LogStreamingService {

  private final EmitterProcessor<StructuredLogLine> processor = EmitterProcessor.create(false);
  private final FluxSink<StructuredLogLine> sink = processor.sink();
  private final Flux<StructuredLogLine> external = processor.publish().autoConnect(0);

  public void broadcast(StructuredLogLine line) {
    sink.next(line);
  }

  public Flux<StructuredLogLine> getLines(String environmentId, RequestGraph requestGraph) {
    return external.filter(line -> line.getEnvironmentId().equals(environmentId))
        .filter(line -> line.getTimestamp().isBefore(requestGraph.getEnd()) && line.getTimestamp().isAfter(requestGraph.getStart()));
  }

}
