package io.wttech.habit.client.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class TarUtils {

  /**
   * Archives all files and folders found in the sourceDir parameter. sourceDir directory itself is
   * not archived
   *
   * @param sourceDir folder to tar
   * @return byte representation of the tar file
   */
  public static byte[] createTarFile(Path sourceDir) {
    File source = sourceDir.toFile();
    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        TarArchiveOutputStream tarOs = new TarArchiveOutputStream(byteArrayOutputStream)) {
      // Using input name to create output name
      for (File file : source.listFiles()) {
        addFilesToTarGZ(file, "", tarOs);
      }
      return byteArrayOutputStream.toByteArray();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static void addFilesToTarGZ(File file, String parent, TarArchiveOutputStream tarArchive)
      throws IOException {
    // Create entry name relative to parent file path
    String entryName = parent + file.getName();
    // add tar ArchiveEntry
    tarArchive.putArchiveEntry(new TarArchiveEntry(file, entryName));
    if (file.isFile()) {
      FileInputStream fis = new FileInputStream(file);
      BufferedInputStream bis = new BufferedInputStream(fis);
      // Write file content to archive
      IOUtils.copy(bis, tarArchive);
      tarArchive.closeArchiveEntry();
      bis.close();
    } else if (file.isDirectory()) {
      // no need to copy any content since it is
      // a directory, just close the outputstream
      tarArchive.closeArchiveEntry();
      // for files in the directories
      for (File f : file.listFiles()) {
        // recursively call the method for all the subdirectories
        addFilesToTarGZ(f, entryName + File.separator, tarArchive);
      }
    }
  }

}
