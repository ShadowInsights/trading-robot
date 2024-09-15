package org.shadow.config.util;

import java.util.concurrent.TimeUnit;

public class TimeUtil {

  private TimeUtil() {}

  public static long calculateShiftBackToPreviousPeriod(long period, TimeUnit unit, long shift) {
    if (period <= 0) {
      throw new IllegalArgumentException("Period must be greater than zero");
    }

    var periodMillis = TimeUnit.MILLISECONDS.convert(period, unit);
    var shiftMillis = TimeUnit.MILLISECONDS.convert(shift, unit);
    var currentTime = TimeProvider.currentTimeMillis();
    var previousTimeFrame = ((currentTime / periodMillis) - 1) * periodMillis;

    return previousTimeFrame - shiftMillis;
  }
}
