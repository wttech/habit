package io.wttech.habit.server.environment.diff.deploy.trigger;

import io.wttech.habit.server.environment.diff.ContainerState;

public class AlwaysDeploy implements DeployTrigger {

  @Override
  public boolean isDeployRequired(ContainerState containerState) {
    return true;
  }
}
