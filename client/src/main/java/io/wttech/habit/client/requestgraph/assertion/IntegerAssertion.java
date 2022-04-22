package io.wttech.habit.client.requestgraph.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.AbstractIntegerAssert;

public class IntegerAssertion {

  private final int value;

  protected IntegerAssertion(int value) {
    this.value = value;
  }

  /**
   * Gives access to the underlying AssertJ object to perform custom assertions.
   *
   * @return AssertJ integer assertion
   */
  public AbstractIntegerAssert<?> asAssert() {
    return assertThat(value);
  }

  public void is(int value) {
    asAssert().isEqualTo(value);
  }
}
