package io.wttech.habit.server.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.springframework.stereotype.Service;

@Service
public class TarService {

  public void unTarGz(InputStream inputStream, Path pathOutput) throws IOException {
    try (TarArchiveInputStream tarArchiveInputStream = createGzipTarStream(inputStream)) {
      handleTar(tarArchiveInputStream, pathOutput);
    }
  }

  public void unTar(InputStream inputStream, Path pathOutput) throws IOException {
    try (TarArchiveInputStream tarArchiveInputStream = createTarStream(inputStream)) {
      handleTar(tarArchiveInputStream, pathOutput);
    }
  }

  private void handleTar(TarArchiveInputStream stream, Path pathOutput) throws IOException {
    ArchiveEntry archiveEntry = null;
    while ((archiveEntry = stream.getNextEntry()) != null) {
      Path pathEntryOutput = pathOutput.resolve(archiveEntry.getName());
      if (archiveEntry.isDirectory()) {
        if (!Files.exists(pathEntryOutput)) {
          Files.createDirectory(pathEntryOutput);
        }
      } else {
        Files.copy(stream, pathEntryOutput);
      }
    }
  }

  private TarArchiveInputStream createGzipTarStream(InputStream inputStream) throws IOException {
    return new TarArchiveInputStream(
        new GzipCompressorInputStream(
            new BufferedInputStream(inputStream)));
  }

  private TarArchiveInputStream createTarStream(InputStream inputStream) {
    return new TarArchiveInputStream(
        new BufferedInputStream(inputStream));
  }

}
