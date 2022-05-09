package io.wttech.habit.server.util;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EnvironmentalVariable {

  public static String format(String variable, String value) {
    Preconditions.checkNotNull(variable);
    Preconditions.checkNotNull(value);
    return String.format("%s=%s", variable, value);
  }

}
