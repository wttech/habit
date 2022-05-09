package io.wttech.habit.server.environment;

import io.wttech.habit.server.HabitException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.stereotype.Component;

@Component
public class BuildHashProvider {

  public String getBuildHash() {
    try (InputStream buildHashFile = getClass().getResourceAsStream("/buildHash")) {
      return new BufferedReader(new InputStreamReader(buildHashFile)).readLine();
    } catch (IOException e) {
      throw new HabitException("Could not read from internal build hash file.", e);
    }
  }

}
