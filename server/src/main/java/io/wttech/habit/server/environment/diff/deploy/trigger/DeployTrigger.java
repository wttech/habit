package io.wttech.habit.server.environment.diff.deploy.trigger;

import io.wttech.habit.server.environment.diff.ContainerState;

public interface DeployTrigger {

  boolean isDeployRequired(ContainerState containerState);

}
