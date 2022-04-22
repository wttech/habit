package io.wttech.habit.gradle.environment.dev;

import io.wttech.habit.gradle.environment.dev.FileEvent.Type;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

public class FileWatchingService {

  private static final Map<Kind<?>, Type> MAPPING = initializeMapping();

  private static Map<Kind<?>, Type> initializeMapping() {
    Map<Kind<?>, Type> result = new HashMap<>();
    result.put(StandardWatchEventKinds.ENTRY_CREATE, Type.CREATED);
    result.put(StandardWatchEventKinds.ENTRY_DELETE, Type.DELETED);
    result.put(StandardWatchEventKinds.ENTRY_MODIFY, Type.MODIFIED);
    return result;
  }

  public Flux<FileEvent> watch(Collection<Path> paths) {
    return createEventSource(paths).filter(this::shouldBeEmitted);
  }

  public void watch(Path... paths) {
    watch(Arrays.asList(paths));
  }

  private Flux<FileEvent> createEventSource(Collection<Path> paths) {
    return Flux.create(emitter -> createEventSource(emitter, paths));
  }

  private void createEventSource(FluxSink<FileEvent> sink, Collection<Path> paths) {
    Map<WatchKey, Path> registeredKeys = new HashMap<>();
    try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
      for (Path path : paths) {
        registerAll(watchService, registeredKeys, path);
      }

      WatchKey key;
      while ((key = watchService.take()) != null) {
        Path originalPath = registeredKeys.get(key);
        for (WatchEvent<?> event : key.pollEvents()) {
          Path modifiedPath = (Path) event.context();
          Path fullModifiedPath = originalPath.resolve(modifiedPath);
          boolean isDirectory = fullModifiedPath.toFile().isDirectory();
          FileEvent.Type type = MAPPING.get(event.kind());
          FileEvent fileEvent = new FileEvent(fullModifiedPath, type, isDirectory);
          sink.next(fileEvent);
        }
        key.reset();
      }
    } catch (IOException e) {
      sink.error(e);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      sink.error(e);
    }
  }

  private boolean shouldBeEmitted(FileEvent fileEvent) {
    return (!fileEvent.isDirectory() || fileEvent.getType() != Type.MODIFIED) && !fileEvent
        .getPath().toString().endsWith("~");
  }

  private void registerPath(WatchService watchService, Map<WatchKey, Path> registeredKeys,
      Path path) {
    try {
      WatchKey key = path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,
          StandardWatchEventKinds.ENTRY_DELETE,
          StandardWatchEventKinds.ENTRY_MODIFY);
      registeredKeys.put(key, path);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Register the given directory, and all its sub-directories, with the WatchService.
   */
  private void registerAll(WatchService watchhService, Map<WatchKey, Path> registeredKeys,
      Path start) throws IOException {
    Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        registerPath(watchhService, registeredKeys, dir);
        return FileVisitResult.CONTINUE;
      }
    });
  }

}
