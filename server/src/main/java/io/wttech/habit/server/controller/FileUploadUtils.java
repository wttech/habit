package io.wttech.habit.server.controller;

import io.wttech.habit.server.util.TempUtil;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;
import java.util.function.Function;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FileUploadUtils {

  private static final String TEMPORARY_FILE_PREFIX = "habit-tar";

  public static <T> Mono<T> usingTemporaryFile(FilePart part, Function<Path, T> fileHandler) {
    Path tempFile = TempUtil.createTemporaryFile(TEMPORARY_FILE_PREFIX);
    Mono<T> buildMono = Mono.defer(() -> Mono.fromCallable(() -> fileHandler.apply(tempFile)));
    return part.transferTo(tempFile)
        .then(buildMono)
        .doOnSuccessOrError((value, ex) -> deleteFile(tempFile));
  }

  public static <T> Mono<T> flatMap(FilePart part, Function<Path, Mono<T>> fileHandler) {
    Path tempFile = TempUtil.createTemporaryFile(TEMPORARY_FILE_PREFIX);
    Mono<T> buildMono = Mono.defer(() -> fileHandler.apply(tempFile));
    return part.transferTo(tempFile)
        .then(buildMono)
        .doOnSuccessOrError((value, ex) -> deleteFile(tempFile));
  }

  public static Mono<Void> usingTemporaryFile(FilePart part, Consumer<Path> fileHandler) {
    Path tempFile = TempUtil.createTemporaryFile(TEMPORARY_FILE_PREFIX);
    Mono<Void> buildMono = Mono.defer(() -> Mono.fromRunnable(() -> fileHandler.accept(tempFile)));
    return part.transferTo(tempFile)
        .then(buildMono)
        .doOnSuccessOrError((value, ex) -> deleteFile(tempFile));
  }

  private static void deleteFile(Path path) {
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
