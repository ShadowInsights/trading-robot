package org.shadow.application.robot.indicator;

import java.util.LinkedList;
import java.util.List;

public class ATRIndicator {

  private final int period;

  public ATRIndicator(int period) {
    this.period = period;
  }

  public List<Double> calculate(double[] highs, double[] lows, double[] closes) {
    var atrValues = new LinkedList<Double>();

    if (highs.length != lows.length || highs.length != closes.length) {
      throw new IllegalArgumentException(
          "Highs, Lows, and Closes arrays must have the same length.");
    }

    if (highs.length < period) {
      throw new IllegalArgumentException("Not enough data to calculate ATR for the given period.");
    }

    var previousATR = 0.0;
    var trueRangeSum = 0.0;

    for (int i = 0; i < highs.length; i++) {
      if (i == 0) {
        // Initialize the first TR value, since there's no previous close price
        trueRangeSum += highs[i] - lows[i];
      } else {
        var trueRange = calculateTrueRange(highs[i], lows[i], closes[i - 1]);
        trueRangeSum += trueRange;

        if (i >= period - 1) {
          if (i == period - 1) {
            // Calculate the first ATR as a simple average of TR values over the period
            previousATR = trueRangeSum / period;
          } else {
            // Calculate ATR using the smoothing formula
            previousATR = (previousATR * (period - 1) + trueRange) / period;
          }
          atrValues.add(previousATR);
        }
      }
    }

    return atrValues;
  }

  public int getPeriod() {
    return period;
  }

  private double calculateTrueRange(double high, double low, double previousClose) {
    var range1 = high - low;
    var range2 = Math.abs(high - previousClose);
    var range3 = Math.abs(low - previousClose);

    return Math.max(range1, Math.max(range2, range3));
  }
}
