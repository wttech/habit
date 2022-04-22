package io.wttech.habit.server.environment;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.dizitart.no2.objects.ObjectRepository;
import org.dizitart.no2.objects.filters.ObjectFilters;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnvironmentRepository {

  private final ObjectRepository<Environment> nitriteRepository;

  public Optional<Environment> findById(String id) {
    Iterator<Environment> iterator = nitriteRepository.find(ObjectFilters.eq("id", id)).iterator();
    if (iterator.hasNext()) {
      return Optional.of(iterator.next());
    } else {
      return Optional.empty();
    }
  }

  public Environment getById(String id) {
    return findById(id).orElseThrow(() -> new EnvironmentNotFoundException());
  }

  public List<Environment> getAll() {
    return nitriteRepository.find().toList();
  }

  public void removeById(String id) {
    nitriteRepository.remove(ObjectFilters.eq("id", id));
  }

  public void remove(Environment environment) {
    nitriteRepository.remove(environment);
  }

  public void add(Environment environment) {
    nitriteRepository.update(environment, true);
  }

  public List<Environment> findAllUp() {
    return nitriteRepository.find(ObjectFilters.eq("state", "up")).toList();
  }
}
