package org.shadow.application.orchestration.util;

public class TimeProvider {

  private TimeProvider() {}

  public static long currentTimeMillis() {
    return System.currentTimeMillis();
  }
}
