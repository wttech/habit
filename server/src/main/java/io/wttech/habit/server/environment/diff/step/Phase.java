package io.wttech.habit.server.environment.diff.step;

public class Phase {

  public static final int DESTROY = 1000;
  public static final int CREATE = 2000;
  public static final int NETWORK = 3000;
  public static final int DEPLOY = 4000;
  public static final int START = 5000;
  public static final int POST_START = 6000;

  private Phase() {
  }

}
