package io.wttech.habit.server.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TempUtil {

  public static Path createTemporaryFile(String prefix) {
    try {
      return Files.createTempFile(prefix, "");
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
