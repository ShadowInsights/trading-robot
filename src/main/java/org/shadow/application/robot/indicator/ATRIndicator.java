package org.shadow.application.robot.indicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.shadow.application.robot.indicator.exception.InsufficientDataException;

/**
 * The ATRIndicator calculates the Average True Range (ATR) of a financial instrument. ATR is a
 * technical analysis indicator that measures market volatility by decomposing the entire range of
 * an asset price for that period.
 */
public class ATRIndicator {

  private final int period;

  /**
   * Constructs an ATRIndicator with the specified period.
   *
   * @param period the number of periods to use in the ATR calculation
   * @throws IllegalArgumentException if the period is not positive
   */
  public ATRIndicator(int period) {
    if (period <= 0) {
      throw new IllegalArgumentException("Period must be positive");
    }
    this.period = period;
  }

  /**
   * Calculates the ATR values for the given high, low, and close price arrays.
   *
   * @param highs an array of high prices
   * @param lows an array of low prices
   * @param closes an array of close prices
   * @return a list of ATR values; the list will have the same length as the input arrays, and ATR
   *     values will be null for the first period - 1 elements where ATR cannot be computed.
   * @throws InsufficientDataException if input arrays are null, have different lengths, or
   *     insufficient length
   */
  public List<Double> calculate(double[] highs, double[] lows, double[] closes) {
    if (highs == null) {
      throw new InsufficientDataException("Highs array cannot be null");
    }
    if (lows == null) {
      throw new InsufficientDataException("Lows array cannot be null");
    }
    if (closes == null) {
      throw new InsufficientDataException("Closes array cannot be null");
    }

    var length = highs.length;

    if (lows.length != length || closes.length != length) {
      throw new InsufficientDataException("Input arrays must have the same length");
    }

    if (length < period) {
      throw new InsufficientDataException("Input arrays must have at least 'period' elements");
    }

    var trueRanges = new ArrayList<Double>(length);

    for (int i = 0; i < length; i++) {
      double tr;
      if (i == 0) {
        tr = highs[i] - lows[i];
      } else {
        var currentHigh = highs[i];
        var currentLow = lows[i];
        var previousClose = closes[i - 1];

        var tr1 = currentHigh - currentLow;
        var tr2 = Math.abs(currentHigh - previousClose);
        var tr3 = Math.abs(currentLow - previousClose);

        tr = Math.max(tr1, Math.max(tr2, tr3));
      }
      trueRanges.add(tr);
    }

    var atrValues = new ArrayList<Double>(Collections.nCopies(length, null));

    var sumTR = 0.0;
    for (int i = 0; i < period; i++) {
      sumTR += trueRanges.get(i);
    }
    var prevATR = sumTR / period;
    atrValues.set(period - 1, prevATR);

    for (int i = period; i < trueRanges.size(); i++) {
      var currentTR = trueRanges.get(i);
      var atr = ((prevATR * (period - 1)) + currentTR) / period;
      atrValues.set(i, atr);
      prevATR = atr;
    }

    return atrValues;
  }

  /**
   * Returns the period used in the ATR calculation.
   *
   * @return the period
   */
  public int getPeriod() {
    return period;
  }
}
