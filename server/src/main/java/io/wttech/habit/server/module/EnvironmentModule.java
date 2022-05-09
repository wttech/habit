package io.wttech.habit.server.module;

import io.wttech.habit.server.environment.configuration.RootConfiguration;
import io.wttech.habit.server.environment.diff.ExpectedState;
import java.util.List;

public interface EnvironmentModule {

  String getId();

  int getOrder();

  List<ExpectedState> createModuleDefinition(String key, RootConfiguration rootConfiguration);

  List<ExpectedState> createModuleDefinition(String key);

}
