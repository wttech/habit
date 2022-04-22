package io.wttech.habit.server.docker.container;

import lombok.AllArgsConstructor;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;

@AllArgsConstructor(staticName = "of")
public class StreamingCommand {

  private final CommandOutputStream callback;
  private final EmitterProcessor<byte[]> logStream;

  public Flux<byte[]> getStream() {
    return logStream;
  }

  public void close() {
    callback.onComplete();
  }

}
