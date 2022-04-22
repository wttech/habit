package io.wttech.habit.server.environment;

import io.wttech.habit.server.docker.container.ContainerService;
import io.wttech.habit.server.docker.container.StreamingCommand;
import io.wttech.habit.server.request.logs.LogRepository;
import io.wttech.habit.server.request.logs.LogStreamingService;
import io.wttech.habit.server.request.logs.LogStructureFactory;
import io.wttech.habit.server.request.logs.ParsedLine;
import io.wttech.habit.server.request.logs.StructuredLogLine;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class LogTailingService {

  private final ConcurrentMap<String, Queue<StreamingCommand>> commands = new ConcurrentHashMap<>();

  private final ContainerService containerService;
  private final LogStructureFactory logStructureFactory;
  private final LogRepository logRepository;
  private final LogStreamingService logStreamingService;

  public void startAsyncLogging(String environmentId, String containerId, String filepath) {
    StreamingCommand streamingCommand = containerService.execStreaming(containerId, "tail", "-0f", filepath);
    register(environmentId, streamingCommand);
    streamingCommand.getStream()
        .flatMap(this::convertToStringLines)
        .handle(new JoinIncompleteLines())
        .doOnNext(line -> processLine(environmentId, filepath, line))
        .subscribeOn(Schedulers.elastic())
        .subscribe();
  }

  public void cleanTailing(String environmentId) {
    Queue<StreamingCommand> queuedCommands = commands.remove(environmentId);
    if (queuedCommands != null) {
      queuedCommands.forEach(StreamingCommand::close);
    }
  }

  private Flux<PartialLine> convertToStringLines(byte[] bytes) {
    String fullString = new String(bytes, StandardCharsets.UTF_8);
    String[] byNewLine = fullString.split("\n");
    if (fullString.endsWith("\n")) {
      return Flux.fromArray(byNewLine).map(PartialLine::full);
    } else {
      Stream<PartialLine> streamWithoutLast = IntStream.range(0, byNewLine.length - 1)
          .mapToObj(i -> PartialLine.full(byNewLine[i]));
      Stream<PartialLine> fullStream = Stream.concat(streamWithoutLast,
          Stream.of(PartialLine.partial(byNewLine[byNewLine.length - 1])));
      return Flux.fromStream(fullStream);
    }
  }

  private void processLine(String environmentId, String filepath, String line) {
    ParsedLine processed = logStructureFactory.parse(line);
    String millisString = processed.getData().get("date");
    if (millisString != null) {
      long millis = Long.parseLong(millisString);
      Instant instant = Instant.ofEpochMilli(millis);
      StructuredLogLine logLine = StructuredLogLine.builder()
          .withEnvironmentId(environmentId)
          .withFilePath(filepath)
          .withTimestamp(instant)
          .withLine(processed.getLine())
          .withMessage(processed.getMessage())
          .withData(processed.getData())
          .withTags(processed.getTags())
          .build();
      logRepository.add(logLine);
      logStreamingService.broadcast(logLine);
    }
  }

  private void register(String environmentId, StreamingCommand command) {
    commands.computeIfAbsent(environmentId, key -> new ConcurrentLinkedQueue<>());
    commands.get(environmentId).add(command);
  }

  private class JoinIncompleteLines implements BiConsumer<PartialLine, SynchronousSink<String>> {

    private String incompleteLine;

    @Override
    public void accept(PartialLine partial, SynchronousSink<String> sink) {
      if (partial.isPartial()) {
        incompleteLine = partial.getLine();
      } else if (incompleteLine != null) {
        sink.next(incompleteLine + partial.getLine());
        incompleteLine = null;
      } else {
        sink.next(partial.getLine());
      }
    }
  }

  private static class PartialLine {

    private final boolean partial;
    private final String line;

    private PartialLine(boolean partial, String line) {
      this.partial = partial;
      this.line = line;
    }

    public static PartialLine partial(String line) {
      return new PartialLine(true, line);
    }

    public static PartialLine full(String line) {
      return new PartialLine(false, line);
    }

    public boolean isPartial() {
      return partial;
    }

    public boolean isFull() {
      return !partial;
    }

    public String getLine() {
      return line;
    }
  }

}
