package org.shadow.application.orchestration.util;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class TimeUtil {

  private TimeUtil() {}

  public static long calculateInitialDelay(long interval, TimeUnit unit) {
    var now = Instant.now();
    var nextInterval = calculateNextInterval(now, interval, unit);

    var delayInMillis = Duration.between(now, nextInterval).toMillis();
    return unit.convert(delayInMillis, TimeUnit.MILLISECONDS);
  }

  private static Instant calculateNextInterval(Instant current, long interval, TimeUnit unit) {
    var intervalMillis = unit.toMillis(interval);
    var currentMillis = current.toEpochMilli();

    var nextIntervalMillis = ((currentMillis / intervalMillis) + 1) * intervalMillis;

    return Instant.ofEpochMilli(nextIntervalMillis);
  }
}
