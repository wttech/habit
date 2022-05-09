package io.wttech.habit.junit;

import io.wttech.habit.client.ClientFacade;
import io.wttech.habit.client.HabitEnvironmentDetails;
import io.wttech.habit.client.http.HabitHttpClient;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HabitRunner implements BeforeAllCallback, BeforeEachCallback {

  private static final Logger LOGGER = LoggerFactory.getLogger(HabitRunner.class);

  public static final Namespace TESTER_NAMESPACE = Namespace.create("io.wttech.habit");
  public static final String ENVIRONMENT_DETAILS_VARIABLE = "details";
  public static final String DSL_VARIABLE = "dsl";
  private static final String INITIALIZED_VARIABLE = "initialized";

  @Override
  public void beforeAll(ExtensionContext context) {
    start(context);
  }

  @Override
  public void beforeEach(ExtensionContext context) {
    start(context);
  }

  private void start(ExtensionContext context) {
    if (!isInitialized(context)) {
      LOGGER.info("Initializing environment");
      try {
        HabitEnvironmentDetails environmentDetails = ClientFacade.instance()
            .start(BuildProgressListener.instance());
        context.getRoot().getStore(TESTER_NAMESPACE)
            .put(ENVIRONMENT_DETAILS_VARIABLE, environmentDetails);
        context.getRoot().getStore(TESTER_NAMESPACE).put(DSL_VARIABLE, createDSL(environmentDetails));
        context.getRoot().getStore(TESTER_NAMESPACE).put(INITIALIZED_VARIABLE, true);
      } catch (Exception e) {
        context.getRoot().getStore(TESTER_NAMESPACE).put(INITIALIZED_VARIABLE, true);
        context.getRoot().getStore(TESTER_NAMESPACE).put("error", e);
      }
    }
    checkError(context);
  }

  private HabitRequestDSL createDSL(HabitEnvironmentDetails environmentDetails) {
    HabitHttpClient client = new HabitHttpClient(environmentDetails.getHostname(),
        environmentDetails.getHttpPort());
    return HabitRequestDSL.of(environmentDetails.getEnvironmentName(), client);
  }

  private void checkError(ExtensionContext context) {
    RuntimeException e = (RuntimeException) context.getRoot().getStore(TESTER_NAMESPACE).get("error");
    if (e != null) {
      throw e;
    }
  }

  private boolean isInitialized(ExtensionContext context) {
    return context.getRoot().getStore(TESTER_NAMESPACE).get(INITIALIZED_VARIABLE) != null;
  }

}
