package io.wttech.habit.server.environment.builds.event;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dizitart.no2.FindOptions;
import org.dizitart.no2.SortOrder;
import org.dizitart.no2.objects.ObjectFilter;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProgressEventRepository {

  private final ObjectRepository<ProgressEvent> nitriteRepository;

  public void add(ProgressEvent event) {
    nitriteRepository.insert(event);
  }

  public List<ProgressEvent> getEvents(String environmentId) {
    return nitriteRepository.find(environmentIdEqual(environmentId), sortAscendingByTime())
        .toList();
  }

  public Optional<ProgressEvent> findNewest(String environmentId, String buildId) {
    ProgressEvent newest = nitriteRepository
        .find(ObjectFilters.and(environmentIdEqual(environmentId), buildIdEqual(buildId)),
            sortDescendingByTime().thenLimit(0, 1)).firstOrDefault();
    return Optional.ofNullable(newest);
  }

  public void delete(String environmentId) {
    nitriteRepository.remove(environmentIdEqual(environmentId));
  }

  public void delete(String environmentId, String buildId) {
    nitriteRepository
        .remove(ObjectFilters.and(environmentIdEqual(environmentId), buildIdEqual(buildId)));
  }

  private ObjectFilter environmentIdEqual(String environmentId) {
    return ObjectFilters.eq("environmentId", environmentId);
  }

  private ObjectFilter buildIdEqual(String buildId) {
    return ObjectFilters.eq("buildId", buildId);
  }

  private FindOptions sortDescendingByTime() {
    return FindOptions.sort("timestamp", SortOrder.Descending);
  }

  private FindOptions sortAscendingByTime() {
    return FindOptions.sort("timestamp", SortOrder.Ascending);
  }

}
