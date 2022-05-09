package io.wttech.habit.gradle.environment;

import io.wttech.habit.client.ClientFacade;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;

public class EnvironmentRecreate extends DefaultTask {

  @TaskAction
  public void recreate() {
    Logger logger = getProject().getLogger();
    GradleBuildListener listener = GradleBuildListener.of(logger);
    ClientFacade.instance().recreate(listener);
  }

}
