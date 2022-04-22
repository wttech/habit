package io.wttech.habit.server.request.logs;

import io.wttech.habit.server.request.RequestGraph;
import io.wttech.habit.server.request.RequestNotFoundException;
import io.wttech.habit.server.request.RequestRepository;
import io.wttech.habit.server.request.structured.StructuredGraph;
import io.wttech.habit.server.util.JsonTranslator;
import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@AllArgsConstructor
@Slf4j
public class LogsApi {

  private final LogRepository logRepository;
  private final RequestRepository requestRepository;
  private final JsonTranslator jsonTranslator;
  private final LogStreamingService logStreamingService;

  public Flux<StructuredLogLine> get(String environmentId, String requestId) {
    RequestGraph requestGraph = requestRepository.get(environmentId, requestId);
    List<StructuredLogLine> logs = logRepository
        .findByTimestamp(environmentId, requestGraph.getStart(), requestGraph.getEnd());
    return Flux.fromIterable(logs);
  }

  public Flux<StructuredLogLine> get(String environmentId, String requestId, String exchangeId) {
    RequestGraph requestGraph = requestRepository.get(environmentId, requestId);
    JsonNode graphNode = requestGraph.getGraph();
    StructuredGraph structuredGraph = jsonTranslator.toClass(graphNode, StructuredGraph.class);
    Optional<StructuredGraph> optionalSubrequest = structuredGraph.findNodeByExchangeId(exchangeId);
    if (!optionalSubrequest.isPresent()) {
      throw new RequestNotFoundException(
          String.format("Subrequest %s not found for request %s", exchangeId, requestId));
    }
    StructuredGraph subrequest = optionalSubrequest.get();
    Instant start = subrequest.getRequest().getTimestamp();
    Instant end = subrequest.getResponse().getTimestamp();
    List<StructuredLogLine> logs = logRepository.findByTimestamp(environmentId, start, end);
    return Flux.fromIterable(logs);
  }

  public Flux<StructuredLogLine> getStream(String environmentId, String requestId) {
    RequestGraph requestGraph = requestRepository.get(environmentId, requestId);
    Flux<StructuredLogLine> newLines = logStreamingService.getLines(environmentId, requestGraph);
    Flux<StructuredLogLine> existingLines = Flux.fromStream(() -> logRepository
        .findByTimestamp(environmentId, requestGraph.getStart(), requestGraph.getEnd()).stream());
    return Flux.merge(newLines, existingLines)
        // do not wait for all items
        .window(Duration.ofSeconds(1))
        // sort them
        .flatMap(window -> window.sort(Comparator.comparing(StructuredLogLine::getEpoch)));
  }

  public List<StructuredLogLine> get(String environmentId) {
    return logRepository.findByEnvironment(environmentId);
  }

  public void cleanOldLogs(String environmentId, Instant threshold) {
    log.info("Cleaning old logs for environment: {}", environmentId);
    logRepository.deleteOlderThan(environmentId, threshold);
  }
}
