package io.wttech.habit.gradle.environment.dev;

import java.nio.file.Path;

public class FileEvent {

  private final Path path;
  private final Type type;
  private final boolean isDirectory;

  public FileEvent(Path path, Type type, boolean isDirectory) {
    this.path = path;
    this.type = type;
    this.isDirectory = isDirectory;
  }

  public Path getPath() {
    return path;
  }

  public Type getType() {
    return type;
  }

  public boolean isDirectory() {
    return isDirectory;
  }

  public enum Type {
    CREATED, DELETED, MODIFIED;
  }

  @Override
  public String toString() {
    return "FileEvent{" +
        "path=" + path +
        ", type=" + type +
        ", isDirectory=" + isDirectory +
        '}';
  }
}
