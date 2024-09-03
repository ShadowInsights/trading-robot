package org.shadow.application.orchestration.util;

import java.util.concurrent.TimeUnit;

public class TimeUtil {

  private TimeUtil() {}

  public static long calculateInitialDelayUntilNextPeriod(long period, TimeUnit unit) {
    if (period <= 0) {
      throw new IllegalArgumentException("Period must be greater than zero");
    }

    var currentTime = TimeProvider.currentTimeMillis();
    var periodMillis = TimeUnit.MILLISECONDS.convert(period, unit);
    var nextTimeFrame = ((currentTime / periodMillis) + 1) * periodMillis;

    return nextTimeFrame - currentTime;
  }
}
