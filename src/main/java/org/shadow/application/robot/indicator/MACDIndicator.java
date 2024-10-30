package org.shadow.application.robot.indicator;

import org.shadow.application.robot.indicator.exception.InsufficientDataException;
import org.shadow.application.robot.indicator.model.MACDCalculationResult;

/**
 * The MACDIndicator class calculates the Moving Average Convergence Divergence (MACD) for a given
 * set of prices. MACD is a trend-following momentum indicator that shows the relationship between
 * two moving averages of prices. The MACD is calculated as the difference between a short-term
 * Exponential Moving Average (EMA) and a long-term EMA. A signal line, which is an EMA of the MACD,
 * is also calculated to generate buy/sell signals.
 */
public class MACDIndicator implements Indicator {

  private final int shortPeriod;
  private final int longPeriod;
  private final int signalPeriod;

  /**
   * Constructs a MACDIndicator with the specified periods for the short-term EMA, long-term EMA,
   * and signal line.
   *
   * @param shortPeriod The period for the short-term EMA (e.g., 12).
   * @param longPeriod The period for the long-term EMA (e.g., 26).
   * @param signalPeriod The period for the signal line EMA (e.g., 9).
   * @throws IllegalArgumentException if any of the period values are non-positive.
   */
  public MACDIndicator(int shortPeriod, int longPeriod, int signalPeriod) {
    if (shortPeriod <= 0 || longPeriod <= 0 || signalPeriod <= 0) {
      throw new IllegalArgumentException("Period values must be positive.");
    }
    this.shortPeriod = shortPeriod;
    this.longPeriod = longPeriod;
    this.signalPeriod = signalPeriod;
  }

  /**
   * Calculates the MACD for the given set of prices.
   *
   * @param prices An array of prices.
   * @return The MACD value.
   * @throws InsufficientDataException if there is not enough data to calculate the MACD.
   */
  public MACDCalculationResult calculate(double[] prices) {
    if (prices.length < longPeriod + signalPeriod) {
      throw new InsufficientDataException("Not enough data to calculate MACD.");
    }

    var shortEMA = calculateSMA(prices, shortPeriod);
    var longEMA = calculateSMA(prices, longPeriod);

    var macd = 0.0;
    var signalEMA = 0.0;

    for (int i = longPeriod; i < prices.length; i++) {
      var price = prices[i];

      shortEMA = calculateEMA(price, shortEMA, shortPeriod);
      longEMA = calculateEMA(price, longEMA, longPeriod);

      macd = shortEMA - longEMA;

      if (i >= (longPeriod + signalPeriod - 1)) {
        signalEMA = calculateEMA(macd, signalEMA, signalPeriod);
      }
    }

    return new MACDCalculationResult(macd, signalEMA);
  }

  // TODO: Move to external class - TR-28
  /**
   * Calculates the Exponential Moving Average (EMA) for a given price based on the previous EMA and
   * the period.
   *
   * @param price The current price.
   * @param prevEMA The previous EMA value.
   * @param period The period for the EMA.
   * @return The new EMA value.
   */
  private double calculateEMA(double price, double prevEMA, int period) {
    var alpha = 2.0 / (period + 1);
    return price * alpha + prevEMA * (1 - alpha);
  }

  // TODO: Move to external class - TR-28
  /**
   * Calculates the Simple Moving Average (SMA) for the first `period` elements in the prices array.
   * The SMA is used as the initial value for the EMA calculation.
   *
   * @param prices An array of prices.
   * @param period The number of periods over which to calculate the SMA.
   * @return The SMA value.
   */
  private double calculateSMA(double[] prices, int period) {
    var sum = 0.0;
    for (int i = 0; i < period; i++) {
      sum += prices[i];
    }
    return sum / period;
  }

  @Override
  public int getPeriod() {
    return longPeriod + signalPeriod;
  }

  @Override
  public int getRequiredPeriodThreshold() {
    return longPeriod + signalPeriod + 1;
  }
}
