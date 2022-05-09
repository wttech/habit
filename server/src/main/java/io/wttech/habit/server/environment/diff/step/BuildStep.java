package io.wttech.habit.server.environment.diff.step;

import java.util.Comparator;
import reactor.core.publisher.Mono;

public interface BuildStep {

  Comparator<BuildStep> BY_ORDER = Comparator.comparing(BuildStep::getOrder);

  Mono<Void> perform();

  int getOrder();

  String getMessage();

}
