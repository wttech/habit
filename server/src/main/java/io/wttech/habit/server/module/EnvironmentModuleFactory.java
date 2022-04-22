package io.wttech.habit.server.module;

public interface EnvironmentModuleFactory<T extends EnvironmentModule> {

  T create();

}
