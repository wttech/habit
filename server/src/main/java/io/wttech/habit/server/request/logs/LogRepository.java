package io.wttech.habit.server.request.logs;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogRepository {

  private static final String ENVIRONMENT_ID_FIELD = "environmentId";
  private static final String EPOCH_FIELD = "epoch";
  private static final String REQUEST_ID_FIELD = "requestId";

  private final ObjectRepository<StructuredLogLine> nitriteRepository;

  public void add(StructuredLogLine logLine) {
    nitriteRepository.insert(logLine);
  }

  public List<StructuredLogLine> findByRequestId(String environmentId, String requestId) {
    return nitriteRepository.find(ObjectFilters
        .and(ObjectFilters.eq(ENVIRONMENT_ID_FIELD, environmentId),
            ObjectFilters.eq(REQUEST_ID_FIELD, requestId))).toList();
  }

  public List<StructuredLogLine> findByTimestamp(String environmentId, Instant start, Instant end) {
    log.info("Finding logs between {} and {}", start.toEpochMilli(), end.toEpochMilli());
    return nitriteRepository.find(ObjectFilters
        .and(ObjectFilters.eq(ENVIRONMENT_ID_FIELD, environmentId),
            ObjectFilters.gt(EPOCH_FIELD, start.toEpochMilli()),
            ObjectFilters.lt(EPOCH_FIELD, end.toEpochMilli())))
        .toList();
  }

  public List<StructuredLogLine> findByEnvironment(String environmentId) {
    return nitriteRepository.find(ObjectFilters.eq(ENVIRONMENT_ID_FIELD, environmentId))
        .toList();
  }

  public void deleteByEnvironment(String environmentId) {
    nitriteRepository.remove(ObjectFilters.eq(ENVIRONMENT_ID_FIELD, environmentId));
  }

  public void deleteOlderThan(String environmentId, Instant threshold) {
    nitriteRepository.remove(ObjectFilters
        .and(ObjectFilters.eq(ENVIRONMENT_ID_FIELD, environmentId),
            ObjectFilters.lt(EPOCH_FIELD, threshold.toEpochMilli())));
  }
}
