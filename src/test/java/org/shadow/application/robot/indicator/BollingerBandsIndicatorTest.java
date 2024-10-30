package org.shadow.application.robot.indicator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.shadow.application.robot.indicator.exception.InsufficientDataException;

/** Unit tests for the BollingerBandsIndicator class. */
class BollingerBandsIndicatorTest {

  @Test
  void testCalculate_BasicScenario() {
    // Given
    var period = 20;
    var stdDevMultiplier = 2.0;
    var prices =
        new double[] {
          22.27, 22.19, 22.08, 22.17, 22.18,
          22.13, 22.23, 22.43, 22.24, 22.29,
          22.15, 22.39, 22.38, 22.61, 23.36,
          24.05, 23.75, 23.83, 23.95, 23.63,
          23.82, 23.87, 23.65, 23.19, 23.10,
          23.33, 22.68, 23.10, 22.40, 22.17,
          22.39, 22.38, 22.61, 23.36, 24.05,
          23.75, 23.83, 23.95, 23.63, 23.82
        };

    var indicator = new BollingerBandsIndicator(period, stdDevMultiplier);

    // When
    var result = indicator.calculate(prices);

    // Then
    var expectedMiddleBand = calculateExpectedSMA(prices, period);
    var expectedStdDev = calculateExpectedStandardDeviation(prices, period, expectedMiddleBand);
    var expectedUpperBand = expectedMiddleBand + (stdDevMultiplier * expectedStdDev);
    var expectedLowerBand = expectedMiddleBand - (stdDevMultiplier * expectedStdDev);

    assertEquals(
        expectedMiddleBand,
        result.middleBand(),
        0.0001,
        "Middle band does not match expected value");
    assertEquals(
        expectedUpperBand, result.upperBand(), 0.0001, "Upper band does not match expected value");
    assertEquals(
        expectedLowerBand, result.lowerBand(), 0.0001, "Lower band does not match expected value");
  }

  @Test
  void testCalculate_InsufficientData() {
    // Given
    var period = 20;
    var stdDevMultiplier = 2.0;
    var prices =
        new double[] {
          22.27, 22.19, 22.08, 22.17, 22.18,
          22.13, 22.23, 22.43, 22.24, 22.29,
          22.15, 22.39, 22.38, 22.61, 23.36
        }; // Only 15 data points

    var indicator = new BollingerBandsIndicator(period, stdDevMultiplier);

    // When & Then
    assertThrows(InsufficientDataException.class, () -> indicator.calculate(prices));
  }

  @Test
  void testConstructor_InvalidPeriod() {
    // Given
    var period = -5;
    var stdDevMultiplier = 2.0;

    // When & Then
    assertThrows(
        IllegalArgumentException.class,
        () -> new BollingerBandsIndicator(period, stdDevMultiplier));
  }

  @Test
  void testConstructor_InvalidStdDevMultiplier() {
    // Given
    var period = 20;
    var stdDevMultiplier = -2.0;

    // When & Then
    assertThrows(
        IllegalArgumentException.class,
        () -> new BollingerBandsIndicator(period, stdDevMultiplier));
  }

  // Helper methods to calculate expected values
  private double calculateExpectedSMA(double[] prices, int period) {
    var sum = 0.0;
    for (int i = prices.length - period; i < prices.length; i++) {
      sum += prices[i];
    }
    return sum / period;
  }

  private double calculateExpectedStandardDeviation(double[] prices, int period, double mean) {
    var varianceSum = 0.0;
    for (int i = prices.length - period; i < prices.length; i++) {
      varianceSum += Math.pow(prices[i] - mean, 2);
    }
    var variance = varianceSum / period;
    return Math.sqrt(variance);
  }
}
