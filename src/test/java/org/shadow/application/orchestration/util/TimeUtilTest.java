package org.shadow.application.orchestration.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class TimeUtilTest {

  @Test
  void testCalculateInitialDelayFor1Second() {
    try (var timeProviderMock = mockStatic(TimeProvider.class)) {
      var mockMillis = 5_500L;
      timeProviderMock.when(TimeProvider::currentTimeMillis).thenReturn(mockMillis);

      var delay = TimeUtil.calculateInitialDelayUntilNextPeriod(1, TimeUnit.SECONDS);

      var expectedDelay = 500;
      assertEquals(expectedDelay, delay);
    }
  }

  @Test
  void testCalculateInitialDelayFor5Seconds() {
    try (var timeProviderMock = mockStatic(TimeProvider.class)) {
      var mockMillis = 4_000_000_000L;
      timeProviderMock.when(TimeProvider::currentTimeMillis).thenReturn(mockMillis);

      var delay = TimeUtil.calculateInitialDelayUntilNextPeriod(5, TimeUnit.SECONDS);

      var expectedDelay = 5000;
      assertEquals(expectedDelay, delay);
    }
  }

  @Test
  void testCalculateInitialDelayFor1Minute() {
    try (var timeProviderMock = mockStatic(TimeProvider.class)) {
      var mockMillis = 55_000_000_000L;
      timeProviderMock.when(TimeProvider::currentTimeMillis).thenReturn(mockMillis);

      var delay = TimeUtil.calculateInitialDelayUntilNextPeriod(1, TimeUnit.MINUTES);

      var expectedDelay = 20000;
      assertEquals(expectedDelay, delay);
    }
  }

  @Test
  void testCalculateInitialDelayFor1Hour() {
    try (var timeProviderMock = mockStatic(TimeProvider.class)) {
      var mockMillis = 3_600_000_000_000L;
      timeProviderMock.when(TimeProvider::currentTimeMillis).thenReturn(mockMillis);

      var delay = TimeUtil.calculateInitialDelayUntilNextPeriod(1, TimeUnit.HOURS);

      var expectedDelay = 3600000;
      assertEquals(expectedDelay, delay);
    }
  }

  @Test
  void testCalculateInitialDelayFor12Hours() {
    try (var timeProviderMock = mockStatic(TimeProvider.class)) {
      var mockMillis = 43_200_000_000_000L;
      timeProviderMock.when(TimeProvider::currentTimeMillis).thenReturn(mockMillis);

      var delay = TimeUtil.calculateInitialDelayUntilNextPeriod(12, TimeUnit.HOURS);

      var expectedDelay = 43200000;
      assertEquals(expectedDelay, delay);
    }
  }
}
