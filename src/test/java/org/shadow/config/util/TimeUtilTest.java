package org.shadow.config.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;
import static org.shadow.config.util.TimeUtil.calculateShiftBackToPreviousPeriod;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class TimeUtilTest {

  @Test
  void calculateShiftBackToPreviousPeriodFor1second() {
    try (var timeProviderMock = mockStatic(TimeProvider.class)) {
      var mockMillis = 5_500L;
      timeProviderMock.when(TimeProvider::currentTimeMillis).thenReturn(mockMillis);

      var delay = calculateShiftBackToPreviousPeriod(1, TimeUnit.SECONDS, 2);

      var expectedResult = 2000;

      assertEquals(expectedResult, delay);
    }
  }
}
