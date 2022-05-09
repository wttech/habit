package io.wttech.habit.gradle.environment;

import io.wttech.habit.client.ClientFacade;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

public class EnvironmentShutdown extends DefaultTask {

  @TaskAction
  public void shutdown() {
    ClientFacade.instance().shutdown();
  }
}
