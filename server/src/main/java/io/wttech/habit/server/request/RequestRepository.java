package io.wttech.habit.server.request;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.dizitart.no2.FindOptions;
import org.dizitart.no2.SortOrder;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RequestRepository {

  private static final String ENVIRONMENT_ID_FIELD = "environmentId";
  private static final String START_EPOCH_FIELD = "startEpoch";
  private static final String ID_FIELD = "id";
  private static final String START_FIELD = "start";

  private final ObjectRepository<RequestGraph> nitriteRepository;

  public void add(RequestGraph graph) {
    nitriteRepository.insert(graph);
  }

  public Optional<RequestGraph> find(String environmentId, String id) {
    Iterator<RequestGraph> iterator = nitriteRepository.find(ObjectFilters
        .and(ObjectFilters.eq(ID_FIELD, id), ObjectFilters.eq(ENVIRONMENT_ID_FIELD, environmentId)))
        .iterator();
    if (iterator.hasNext()) {
      return Optional.of(iterator.next());
    } else {
      return Optional.empty();
    }
  }

  public RequestGraph get(String environmentId, String id) {
    return find(environmentId, id).orElseThrow(() -> new IllegalStateException());
  }

  public List<RequestGraph> getAll(String environmentId) {
    return nitriteRepository.find(ObjectFilters.eq(ENVIRONMENT_ID_FIELD, environmentId), FindOptions.sort(START_FIELD,
        SortOrder.Descending)).toList();
  }

  public void deleteByEnvironment(String environmentId) {
    nitriteRepository.remove(ObjectFilters.eq(ENVIRONMENT_ID_FIELD, environmentId));
  }

  public List<RequestGraph> getRecent(String environmentId, int count) {
    return nitriteRepository.find(ObjectFilters.eq(ENVIRONMENT_ID_FIELD, environmentId), FindOptions.sort(START_FIELD,
        SortOrder.Descending).thenLimit(0, count)).toList();
  }

  public void deleteOlderThan(String environmentId, Instant threshold) {
    nitriteRepository.remove(ObjectFilters
        .and(ObjectFilters.eq(ENVIRONMENT_ID_FIELD, environmentId),
            ObjectFilters.lt(START_EPOCH_FIELD, threshold.toEpochMilli())));
  }
}
