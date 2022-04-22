package io.wttech.habit.client.requestgraph.assertion;

import static org.assertj.core.api.Assertions.assertThat;

import io.wttech.habit.client.requestgraph.ExchangeError;
import org.assertj.core.api.ObjectAssert;

public class ErrorAssertion {

  private final ExchangeError error;

  ErrorAssertion(ExchangeError error) {
    this.error = error;
  }

  public ObjectAssert<ExchangeError> asAssert() {
    return assertThat(error);
  }

}
