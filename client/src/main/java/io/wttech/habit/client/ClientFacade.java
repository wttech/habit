package io.wttech.habit.client;

import io.wttech.habit.client.configuration.DeployPath;
import io.wttech.habit.client.configuration.ServerConfiguration;
import io.wttech.habit.client.http.DeployResult;
import io.wttech.habit.client.http.HabitHttpClient;
import io.wttech.habit.client.internal.HabitContext;
import io.wttech.habit.client.internal.HabitContextFactory;
import io.wttech.habit.client.sse.InternalEventStreamListener;
import io.wttech.habit.client.util.TarUtils;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientFacade {

  public static final int DEFAULT_HTTP_PORT = 7080;

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientFacade.class);

  private final HabitContextFactory habitContextFactory = HabitContextFactory.instance();

  ClientFacade() {
  }

  public static ClientFacade instance() {
    return new ClientFacade();
  }

  public HabitEnvironmentDetails start(HabitSettings habitSettings, ProgressListener listener) {
    HabitContext context = habitContextFactory.getContext(habitSettings);
    byte[] deploymentTar = createDeploymentTar(context.getRootFolder(), context.getServers());
    HabitHttpClient client = new HabitHttpClient(context.getHostname(), context.getHttpPort());

    CountDownLatch latch = new CountDownLatch(1);
    InternalEventStreamListener internalListener = new InternalEventStreamListener(latch, listener);

    client
        .start(context.getEnvironmentName(), context.getConfigurationJson(), deploymentTar, internalListener);

    try {
      LOGGER.debug("About to start waiting for complete signal");
      boolean success = latch.await(1, TimeUnit.MINUTES);
      if (!success) {
        throw new HabitClientException("Environment build timeout.");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    if (internalListener.isError()) {
      throw internalListener.getException();
    }

    return HabitEnvironmentDetails
        .of(context.getHostname(), context.getHttpPort(),
            context.getEnvironmentName());
  }

  public HabitEnvironmentDetails start(ProgressListener listener) {
    return start(HabitSettings.builtin(), listener);
  }

  public HabitEnvironmentDetails start(HabitSettings habitSettings) {
    return start(habitSettings, ProgressListener.NOOP);
  }

  public HabitEnvironmentDetails start() {
    return start(HabitSettings.builtin(), ProgressListener.NOOP);
  }

  public List<Path> getDeploymentPaths() {
    HabitContext context = habitContextFactory.getContext(HabitSettings.builtin());
    return context.getServers().stream().flatMap(server -> server.getDeploy().getPaths().stream())
        .map(deployPath -> Paths.get(context.getRootFolder()).resolve(deployPath.getSource()))
        .collect(Collectors.toList());
  }

  public DeployResult deploy() {
    return deploy(HabitSettings.builtin());
  }

  public DeployResult deploy(HabitSettings habitSettings) {
    HabitContext context = habitContextFactory.getContext(habitSettings);
    byte[] deploymentTar = createDeploymentTar(context.getRootFolder(), context.getServers());
    HabitHttpClient client = new HabitHttpClient(context.getHostname(), context.getHttpPort());
    return client.deploy(context.getEnvironmentName(), deploymentTar);
  }

  public HabitEnvironmentDetails recreate(HabitSettings habitSettings, ProgressListener listener) {
    shutdown(habitSettings);
    return start(habitSettings, listener);
  }

  public HabitEnvironmentDetails recreate(ProgressListener listener) {
    return recreate(HabitSettings.builtin(), listener);
  }

  public HabitEnvironmentDetails recreate(HabitSettings habitSettings) {
    return recreate(habitSettings, ProgressListener.NOOP);
  }

  public HabitEnvironmentDetails recreate() {
    return recreate(HabitSettings.builtin());
  }

  public void shutdown(HabitSettings habitSettings) {
    HabitContext context = habitContextFactory.getContext(habitSettings);
    HabitHttpClient client = new HabitHttpClient(context.getHostname(), context.getHttpPort());
    client.shutdown(context.getEnvironmentName());
  }

  public void shutdown() {
    shutdown(HabitSettings.builtin());
  }

  private byte[] createDeploymentTar(String deployRootFolder,
      List<ServerConfiguration> proxyServers) {
    try {
      Path rootTarFolder = Files.createTempDirectory("habit-deploy");
      for (ServerConfiguration server : proxyServers) {
        Path serverRootFolder = rootTarFolder.resolve(server.getName());
        Files.createDirectory(serverRootFolder);
        for (int i = 0; i < server.getDeploy().getPaths().size(); i++) {
          DeployPath path = server.getDeploy().getPaths().get(i);
          Path deploySectionRootFolder = serverRootFolder.resolve(Integer.toString(i));
          Path source = Paths.get(deployRootFolder).resolve(path.getSource());
          FileUtils.copyDirectory(source.toFile(), deploySectionRootFolder.toFile());
        }
      }
      return TarUtils.createTarFile(rootTarFolder);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
