package io.wttech.habit.client.requestgraph.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.AbstractStringAssert;

public class StringAssertion {

  private final String value;

  protected StringAssertion(String value) {
    this.value = value;
  }

  /**
   * Gives access to the underlying AssertJ object to perform custom assertions.
   *
   * @return AssertJ string assertion
   */
  public AbstractStringAssert<?> asAssert() {
    return assertThat(value);
  }

  public void is(String expected) {
    asAssert().isEqualTo(expected);
  }

  public void startsWith(String expected) {
    asAssert().startsWith(expected);
  }

  public void endsWith(String expected) {
    asAssert().endsWith(expected);
  }

  public void matches(String regex) {
    asAssert().matches(regex);
  }

}
