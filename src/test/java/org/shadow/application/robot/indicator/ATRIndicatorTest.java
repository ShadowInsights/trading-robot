package org.shadow.application.robot.indicator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ATRIndicatorTest {

  private ATRIndicator atrIndicator;

  @BeforeEach
  void setup() {
    atrIndicator = new ATRIndicator(3);
  }

  @Test
  void testATRCalculation() {
    var highs = new double[] {45.25, 46.00, 46.75, 45.50, 47.00};
    var lows = new double[] {44.50, 45.00, 45.25, 44.25, 46.00};
    var closes = new double[] {45.00, 45.75, 46.50, 45.25, 46.50};

    var atrValues = atrIndicator.calculate(highs, lows, closes);

    var expectedAtr = new double[] {1.0833, 1.472222222222222, 1.5648148148148149};

    assertEquals(expectedAtr.length, atrValues.size(), "ATR length should match expected values");

    for (int i = 0; i < expectedAtr.length; i++) {
      assertEquals(expectedAtr[i], atrValues.get(i), 0.0001, "ATR value mismatch at index " + i);
    }
  }

  @Test
  void testInvalidDataLength() {
    var highs = new double[] {45.25, 46.00, 46.75};
    var lows = new double[] {44.50, 45.00};
    var closes = new double[] {45.00, 45.75, 46.50};

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          atrIndicator.calculate(highs, lows, closes);
        });
  }

  @Test
  void testNotEnoughData() {
    var highs = new double[] {45.25, 46.00};
    var lows = new double[] {44.50, 45.00};
    var closes = new double[] {45.00, 45.75};

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          atrIndicator.calculate(highs, lows, closes);
        });
  }

  @Test
  void testSingleDataPoint() {
    var highs = new double[] {45.25};
    var lows = new double[] {44.50};
    var closes = new double[] {45.00};

    assertThrows(
        IllegalArgumentException.class,
        () -> {
          atrIndicator.calculate(highs, lows, closes);
        });
  }

  @Test
  void testValidPeriodCalculation() {
    atrIndicator = new ATRIndicator(2);
    var highs = new double[] {45.25, 46.00, 46.75, 45.50};
    var lows = new double[] {44.50, 45.00, 45.25, 44.25};
    var closes = new double[] {45.00, 45.75, 46.50, 45.25};

    var atrValues = atrIndicator.calculate(highs, lows, closes);

    var expectedAtr = new double[] {0.875, 1.1875, 1.71875};

    assertEquals(expectedAtr.length, atrValues.size(), "ATR length should match expected values");

    for (int i = 0; i < expectedAtr.length; i++) {
      assertEquals(expectedAtr[i], atrValues.get(i), 0.0001, "ATR value mismatch at index " + i);
    }
  }
}
