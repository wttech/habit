package io.wttech.habit.client;

import com.google.common.base.Preconditions;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ConfigurationFileLoader {

  public String load(URL resource) {
    Preconditions.checkNotNull(resource);
    try (InputStream inputStream = resource.openStream()) {
      ByteArrayOutputStream result = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int length;
      while ((length = inputStream.read(buffer)) != -1) {
        result.write(buffer, 0, length);
      }
      return result.toString(StandardCharsets.UTF_8.name());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }


}
