package io.wttech.habit.server.environment.diff.deploy;

import io.wttech.habit.server.environment.diff.ContainerState;
import io.wttech.habit.server.environment.diff.deploy.strategy.DeployStrategy;
import io.wttech.habit.server.environment.diff.deploy.trigger.DeployTrigger;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class ProxyServerDeployment {

  private final DeployTrigger trigger;
  private final DeployStrategy strategy;
  private final String postDeployCommand;
  private final String reloadCommand;

  public static Builder builder() {
    return new Builder();
  }

  public boolean isDeployRequired(ContainerState containerState) {
    return trigger.isDeployRequired(containerState);
  }

  public boolean isPostDeployCommandDefined() {
    return postDeployCommand != null && !postDeployCommand.isEmpty();
  }

  public String getPostDeployCommand() {
    return postDeployCommand;
  }

  public boolean isReloadCommandDefined() {
    return reloadCommand != null && !reloadCommand.isEmpty();
  }

  public String getReloadCommand() {
    return reloadCommand;
  }

  public void deploy(Path rootFolder) {
    strategy.deploy(rootFolder);
  }

  public static final class Builder {

    private DeployTrigger trigger;
    private DeployStrategy strategy;
    private String postDeployCommand;
    private String reloadCommand;

    private Builder() {
    }

    public Builder withTrigger(DeployTrigger trigger) {
      this.trigger = trigger;
      return this;
    }

    public Builder withStrategy(DeployStrategy strategy) {
      this.strategy = strategy;
      return this;
    }

    public Builder withPostDeployCommand(String postDeployCommand) {
      this.postDeployCommand = postDeployCommand;
      return this;
    }

    public Builder withReloadCommand(String reloadCommand) {
      this.reloadCommand = reloadCommand;
      return this;
    }

    public ProxyServerDeployment build() {
      return new ProxyServerDeployment(trigger, strategy, postDeployCommand, reloadCommand);
    }
  }
}
