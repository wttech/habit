package io.wttech.habit.server.module;

import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnvironmentModuleProvider {

  private final ServiceLoader<EnvironmentModuleFactory> serviceLoader = ServiceLoader
      .load(EnvironmentModuleFactory.class);


  public List<EnvironmentModule> getModules() {
    Stream<EnvironmentModuleFactory> loadedModules = StreamSupport
        .stream(serviceLoader.spliterator(), false);
    return loadedModules
        .map(factory -> factory.create())
        .sorted(Comparator.comparing(EnvironmentModule::getOrder))
        .collect(Collectors.toList());
  }

}
