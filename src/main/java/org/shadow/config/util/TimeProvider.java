package org.shadow.config.util;

public class TimeProvider {

  private TimeProvider() {}

  public static long currentTimeMillis() {
    return System.currentTimeMillis();
  }
}
