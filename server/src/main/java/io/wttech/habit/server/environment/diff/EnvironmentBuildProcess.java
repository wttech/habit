package io.wttech.habit.server.environment.diff;

import io.wttech.habit.server.environment.diff.step.BuildStep;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EnvironmentBuildProcess {

  private final String environmentId;
  private final UUID buildId;
  private final List<BuildStep> steps;

  public static EnvironmentBuildProcess of(String environmentId, List<BuildStep> steps) {
    List<BuildStep> sorted = steps.stream()
        .sorted(BuildStep.BY_ORDER)
        .collect(Collectors.toList());
    return new EnvironmentBuildProcess(environmentId, UUID.randomUUID(), sorted);
  }

  public String getEnvironmentId() {
    return environmentId;
  }

  public UUID getBuildId() {
    return buildId;
  }

  public List<BuildStep> getSteps() {
    return Collections.unmodifiableList(steps);
  }
}
