package org.shadow.application.robot.indicator;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.shadow.application.robot.indicator.exception.InsufficientDataException;

class ATRIndicatorTest {

  @Test
  void testCalculateNormalCase() {
    var highs = new double[] {48.70, 48.72, 48.90, 48.87, 48.82};
    var lows = new double[] {47.79, 48.14, 48.39, 48.37, 48.24};
    var closes = new double[] {48.16, 48.61, 48.75, 48.63, 48.74};
    var period = 3;

    var atrIndicator = new ATRIndicator(period);
    var atrValues = atrIndicator.calculate(highs, lows, closes);

    var expectedATR = Arrays.asList(null, null, 0.6667, 0.6111, 0.6007);

    assertEquals(expectedATR.size(), atrValues.size(), "ATR values size mismatch");
    for (var i = 0; i < expectedATR.size(); i++) {
      var expected = expectedATR.get(i);
      var actual = atrValues.get(i);
      if (expected == null) {
        assertNull(actual, "Expected null ATR value at index " + i);
      } else {
        assertNotNull(actual, "Expected non-null ATR value at index " + i);
        assertEquals(expected, actual, 0.0001, "ATR value mismatch at index " + i);
      }
    }
  }

  @Test
  void testCalculateInsufficientData() {
    var highs = new double[] {48.70, 48.72};
    var lows = new double[] {47.79, 48.14};
    var closes = new double[] {48.16, 48.61};
    var period = 3;

    var atrIndicator = new ATRIndicator(period);

    var exception =
        assertThrows(
            InsufficientDataException.class,
            () -> {
              atrIndicator.calculate(highs, lows, closes);
            });

    var expectedMessage = "Input arrays must have at least 'period' elements";
    var actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void testCalculateNullInputs() {
    var lows = new double[] {47.79, 48.14};
    var closes = new double[] {48.16, 48.61};
    var period = 2;

    var atrIndicator = new ATRIndicator(period);

    assertThrows(
        InsufficientDataException.class,
        () -> {
          atrIndicator.calculate(null, lows, closes);
        });
  }

  @Test
  void testCalculateDifferentArrayLengths() {
    var highs = new double[] {48.70, 48.72, 48.90};
    var lows = new double[] {47.79, 48.14};
    var closes = new double[] {48.16, 48.61, 48.75};
    var period = 2;

    var atrIndicator = new ATRIndicator(period);

    var exception =
        assertThrows(
            InsufficientDataException.class,
            () -> {
              atrIndicator.calculate(highs, lows, closes);
            });

    var expectedMessage = "Input arrays must have the same length";
    var actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void testConstructorInvalidPeriod() {
    var period = 0;
    var exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              new ATRIndicator(period);
            });

    var expectedMessage = "Period must be positive";
    var actualMessage = exception.getMessage();
    assertTrue(actualMessage.contains(expectedMessage));
  }
}
