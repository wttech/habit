package io.wttech.habit.gradle.environment.dev;

import io.wttech.habit.client.ClientFacade;
import io.wttech.habit.client.http.DeployResult;
import io.wttech.habit.gradle.environment.GradleBuildListener;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.TaskAction;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class DevMode extends DefaultTask {

  @TaskAction
  public void start() {
    Logger logger = getProject().getLogger();
    GradleBuildListener listener = GradleBuildListener.of(logger);
    ClientFacade clientFacade = ClientFacade.instance();
    logger.lifecycle("Initializing environment");
    clientFacade.start(listener);
    logger.lifecycle("Environment initialized");
    Disposable subscription = startWatching(logger);

    try {
      Thread.sleep(Long.MAX_VALUE);
    } catch (InterruptedException e) {
      subscription.dispose();
      Thread.currentThread().interrupt();
    }
  }

  private Disposable startWatching(Logger logger) {
    ClientFacade clientFacade = ClientFacade.instance();
    return this.watch(clientFacade, logger)
        .subscribeOn(Schedulers.elastic())
        .subscribe();
  }

  private Flux<FileEvent> watch(ClientFacade clientFacade, Logger logger) {
    logger.lifecycle("Watcher enabled");
    List<Path> deploymentPaths = clientFacade.getDeploymentPaths();
    FileWatchingService fileWatchingService = new FileWatchingService();
    return fileWatchingService.watch(deploymentPaths)
        .groupBy(FileEvent::getPath)
        .flatMap(groupedFlux -> groupedFlux.sampleFirst(Duration.ofMillis(100)))
        .doOnNext(event -> {
          logger.lifecycle("Detected file change. Performing configuration deployment...");
          DeployResult result = clientFacade.deploy();
          if (result.isSuccess()) {
            logger.lifecycle("Configuration reloaded.");
          } else {
            logger.lifecycle("Error occurred during deployment.\n" + result.getErrorMessage());
          }
        });
  }

}
