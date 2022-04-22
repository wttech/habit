package io.wttech.habit.gradle;

import io.wttech.habit.gradle.server.ServerProvision;
import io.wttech.habit.gradle.server.ServerShutdown;
import io.wttech.habit.gradle.environment.dev.DevMode;
import io.wttech.habit.gradle.environment.EnvironmentRecreate;
import io.wttech.habit.gradle.environment.EnvironmentShutdown;
import io.wttech.habit.gradle.environment.EnvironmentStart;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

public class HabitGradlePlugin implements Plugin<Project> {

  private static final String TASK_GROUP = "habit";

  @Override
  public void apply(Project project) {
    project.getTasks().create("environmentStart", EnvironmentStart.class, this::setTaskGroup);
    project.getTasks().create("environmentShutdown", EnvironmentShutdown.class, this::setTaskGroup);
    project.getTasks().create("environmentRecreate", EnvironmentRecreate.class, this::setTaskGroup);
    project.getTasks().create("devMode", DevMode.class, this::setTaskGroup);
    project.getTasks().create("serverProvision", ServerProvision.class, this::setTaskGroup);
    project.getTasks().create("serverShutdown", ServerShutdown.class, this::setTaskGroup);
  }

  private void setTaskGroup(Task task) {
    task.setGroup(TASK_GROUP);
  }

}
