package io.wttech.habit.server.environment.diff;

import io.wttech.habit.server.environment.builds.event.ProgressEvent;
import io.wttech.habit.server.environment.builds.event.ProgressEventService;
import io.wttech.habit.server.environment.diff.step.BuildStep;
import io.wttech.habit.server.environment.event.EnvironmentEventBroadcaster;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@Component
@RequiredArgsConstructor
@Slf4j
public class BuildProcessManager {

  private final ProgressEventService progressEventService;
  private final EnvironmentEventBroadcaster environmentEventBroadcaster;

  public Mono<Void> perform(EnvironmentBuildProcess build) {
    log.info("Environment {} build start", build.getEnvironmentId());
    List<BuildStep> steps = build.getSteps();
    int stepCount = steps.size();

    return Flux.fromIterable(steps)
        .index()
        .doOnNext(indexedStep -> broadcastStepEvent(build, indexedStep.getT2(), indexedStep.getT1(),
            stepCount))
        .map(Tuple2::getT2)
        .flatMap(BuildStep::perform)
        .then(Mono.fromRunnable(() -> sendBuildCompleteEvent(build, stepCount)))
        .then(Mono.fromRunnable(() -> environmentEventBroadcaster.broadcastBuildEnd(build.getEnvironmentId())))
        .doOnError(e -> log.error("Build step error", e))
        .doOnError(e -> sendBuildErrorEvent(build, e.getMessage()))
        .then();
  }

  private void broadcastStepEvent(EnvironmentBuildProcess build, BuildStep step, long index,
      long stepCount) {
    ProgressEvent event = ProgressEvent.message()
        .withCompletedSteps(index)
        .withTotalSteps(stepCount)
        .withEnvironmentId(build.getEnvironmentId())
        .withBuildId(build.getBuildId().toString())
        .withMessage(step.getMessage())
        .build();
    progressEventService.broadcast(event);
  }

  private void sendBuildCompleteEvent(EnvironmentBuildProcess build, int stepCount) {
    ProgressEvent completeEvent = ProgressEvent.message()
        .withCompletedSteps(stepCount)
        .withTotalSteps(stepCount)
        .withEnvironmentId(build.getEnvironmentId())
        .withBuildId(build.getBuildId().toString())
        .withMessage("Environment build has been completed")
        .build();
    progressEventService.broadcast(completeEvent);
  }

  private void sendBuildErrorEvent(EnvironmentBuildProcess build, String message) {
    ProgressEvent errorEvent = ProgressEvent.error()
        .withEnvironmentId(build.getEnvironmentId())
        .withBuildId(build.getBuildId().toString())
        .withMessage(message)
        .build();
    progressEventService.broadcast(errorEvent);
  }

}
