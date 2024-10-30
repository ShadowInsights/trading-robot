package org.shadow.application.robot.indicator;

import java.util.Arrays;
import org.shadow.application.robot.indicator.exception.InsufficientDataException;
import org.shadow.application.robot.indicator.model.BollingerBandsResult;

/**
 * The BollingerBandsIndicator calculates the Bollinger Bands for a given set of prices. Bollinger
 * Bands consist of a middle band (simple moving average), an upper band, and a lower band based on
 * standard deviation.
 */
public class BollingerBandsIndicator implements Indicator {

  private final int period;
  private final double standardDeviationMultiplier;

  /**
   * Constructs a BollingerBandsIndicator with the specified period and standard deviation
   * multiplier.
   *
   * @param period The period over which to calculate the moving average and standard deviation.
   * @param standardDeviationMultiplier The number of standard deviations to use for the upper and
   *     lower bands.
   */
  public BollingerBandsIndicator(int period, double standardDeviationMultiplier) {
    if (period <= 0) {
      throw new IllegalArgumentException("Period must be positive.");
    }
    if (standardDeviationMultiplier <= 0) {
      throw new IllegalArgumentException("Standard deviation multiplier must be positive.");
    }
    this.period = period;
    this.standardDeviationMultiplier = standardDeviationMultiplier;
  }

  /**
   * Calculates the Bollinger Bands for the given prices.
   *
   * @param prices The array of prices to calculate the Bollinger Bands for.
   * @return A BollingerBandsResult containing the upper band, middle band, and lower band.
   * @throws InsufficientDataException If there is not enough data to calculate Bollinger Bands for
   *     the given period.
   */
  public BollingerBandsResult calculate(double[] prices) {
    if (prices == null) {
      throw new InsufficientDataException("Prices array cannot be null.");
    }
    if (prices.length < period) {
      throw new InsufficientDataException(
          "Not enough data to calculate Bollinger Bands for the given period.");
    }

    var recentPrices = Arrays.copyOfRange(prices, prices.length - period, prices.length);
    var sum = 0.0;
    for (double price : recentPrices) {
      sum += price;
    }
    var middleBand = sum / period;

    var varianceSum = 0.0;
    for (double price : recentPrices) {
      varianceSum += Math.pow(price - middleBand, 2);
    }
    var variance = varianceSum / period;
    var standardDeviation = Math.sqrt(variance);

    var upperBand = middleBand + (standardDeviationMultiplier * standardDeviation);
    var lowerBand = middleBand - (standardDeviationMultiplier * standardDeviation);

    return new BollingerBandsResult(upperBand, middleBand, lowerBand);
  }

  @Override
  public int getPeriod() {
    return period;
  }

  @Override
  public int getRequiredPeriodThreshold() {
    return period;
  }
}
