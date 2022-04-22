package io.wttech.habit.junit;

import io.wttech.habit.client.HabitEnvironmentDetails;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class HabitEnvironmentParameterResolver implements ParameterResolver {

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType() == HabitEnvironment.class;
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    Store store = extensionContext.getRoot().getStore(HabitRunner.TESTER_NAMESPACE);
    HabitEnvironmentDetails details = (HabitEnvironmentDetails) store.get(HabitRunner.ENVIRONMENT_DETAILS_VARIABLE);
    HabitEnvironment environment = HabitEnvironment.of(details.getEnvironmentName(), details.getHostname(), details.getHttpPort());
    return environment;
  }
}
