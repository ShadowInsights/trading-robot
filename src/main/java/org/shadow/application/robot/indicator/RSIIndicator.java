package org.shadow.application.robot.indicator;

import org.shadow.application.robot.indicator.exception.InsufficientDataException;

/** RSIIndicator calculates the Relative Strength Index (RSI) for a set of prices. */
public class RSIIndicator {

  private final int period;

  /**
   * Constructs an RSIIndicator with the specified period.
   *
   * @param period The period over which to calculate the RSI.
   */
  public RSIIndicator(int period) {
    this.period = period;
  }

  /**
   * Calculates the Relative Strength Index (RSI) for the given prices.
   *
   * @param prices The array of prices to calculate the RSI for.
   * @return The RSI value.
   * @throws InsufficientDataException If there is not enough data to calculate RSI for the given
   *     period.
   */
  public Double calculate(double[] prices) {
    if (prices == null) {
      throw new InsufficientDataException("Prices array cannot be null.");
    }
    if (prices.length < period + 1) {
      throw new InsufficientDataException("Not enough data to calculate RSI for the given period.");
    }

    var gains = new double[prices.length - 1];
    var losses = new double[prices.length - 1];

    for (var i = 1; i < prices.length; i++) {
      var change = prices[i] - prices[i - 1];
      gains[i - 1] = Math.max(change, 0);
      losses[i - 1] = Math.max(-change, 0);
    }

    var avgGain = 0.0;
    var avgLoss = 0.0;
    for (var i = 0; i < period; i++) {
      avgGain += gains[i];
      avgLoss += losses[i];
    }
    avgGain /= period;
    avgLoss /= period;

    for (var i = period; i < gains.length; i++) {
      avgGain = ((avgGain * (period - 1)) + gains[i]) / period;
      avgLoss = ((avgLoss * (period - 1)) + losses[i]) / period;
    }

    if (avgLoss == 0) {
      return 100.0;
    }

    var rs = avgGain / avgLoss;

    return 100 - (100 / (1 + rs));
  }

  /**
   * Returns the period over which the RSI is calculated.
   *
   * @return The period.
   */
  public int getPeriod() {
    return period;
  }
}
