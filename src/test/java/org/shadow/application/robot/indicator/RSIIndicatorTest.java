package org.shadow.application.robot.indicator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.shadow.application.robot.indicator.exception.InsufficientDataException;

class RSIIndicatorTest {

  @Test
  void testCalculateRSI_ValidInput() {
    double[] prices = {
      45.00, 46.00, 47.50, 48.00, 47.00, 46.50, 45.75, 46.25, 45.50, 44.50, 43.00, 44.00, 45.00,
      44.00, 45.50, 46.00
    };
    var rsiIndicator = new RSIIndicator(14);

    var rsi = rsiIndicator.calculate(prices);

    assertNotNull(rsi);
    assertEquals(53.69, rsi, 0.01);
  }

  @Test
  void testCalculateRSI_NotEnoughData() {
    double[] prices = {45.00, 46.00}; // Less than the required period
    var rsiIndicator = new RSIIndicator(14);

    assertThrows(
        InsufficientDataException.class,
        () -> rsiIndicator.calculate(prices),
        "Should throw exception for not enough data");
  }

  @Test
  void testCalculateRSI_NoLoss() {
    double[] prices = {
      40.00, 42.00, 43.00, 44.00, 45.00, 46.00, 47.00, 48.00, 49.00, 50.00, 51.00, 52.00, 53.00,
      54.00, 55.00
    };
    var rsiIndicator = new RSIIndicator(14);

    var rsi = rsiIndicator.calculate(prices);

    assertEquals(100.0, rsi, "RSI should be 100 when there are no losses");
  }

  @Test
  void testCalculateRSI_NoGain() {
    double[] prices = {
      55.00, 54.00, 53.00, 52.00, 51.00, 50.00, 49.00, 48.00, 47.00, 46.00, 45.00, 44.00, 43.00,
      42.00, 41.00
    };
    var rsiIndicator = new RSIIndicator(14);

    var rsi = rsiIndicator.calculate(prices);

    assertEquals(0.0, rsi, "RSI should be 0 when there are no gains");
  }

  @Test
  void testCalculateRSI_MixedGainsAndLosses() {
    double[] prices = {
      50.00, 52.00, 51.00, 53.00, 54.00, 55.00, 56.00, 55.00, 54.00, 56.00, 58.00, 57.00, 59.00,
      58.00, 60.00
    };
    var rsiIndicator = new RSIIndicator(14);

    var rsi = rsiIndicator.calculate(prices);

    assertNotNull(rsi);
    assertEquals(75.0, rsi, 0.01);
  }

  @Test
  void testCalculateRSI_PeriodTooShort() {
    double[] prices = {
      45.00, 46.00, 47.50, 48.00, 47.00, 46.50, 45.75, 46.25, 45.50, 44.50, 43.00, 44.00, 45.00,
      44.00, 45.50, 46.00
    };
    var rsiIndicator = new RSIIndicator(16);

    assertThrows(
        InsufficientDataException.class,
        () -> rsiIndicator.calculate(prices),
        "Should throw exception for period too short");
  }
}
