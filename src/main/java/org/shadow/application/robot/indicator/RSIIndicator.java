package org.shadow.application.robot.indicator;

public class RSIIndicator {

  private final int period;

  public RSIIndicator(int period) {
    this.period = period;
  }

  public Double calculate(double[] prices) {
    if (prices == null || prices.length <= period) {
      throw new IllegalArgumentException("Not enough data to calculate RSI");
    }

    var gainSum = 0.0;
    var lossSum = 0.0;

    // First, calculate initial average gain and loss
    for (int i = 1; i <= period; i++) {
      var change = prices[i] - prices[i - 1];
      if (change > 0) {
        gainSum += change;
      } else {
        lossSum -= change;
      }
    }

    var avgGain = gainSum / period;
    var avgLoss = lossSum / period;

    // Iterate over the rest of the prices
    for (int i = period + 1; i < prices.length; i++) {
      var change = prices[i] - prices[i - 1];
      if (change > 0) {
        gainSum += change;
        avgGain = (gainSum + avgGain * (period - 1)) / period;
      } else {
        lossSum -= change;
        avgLoss = (lossSum + avgLoss * (period - 1)) / period;
      }
    }

    // Avoid division by zero
    if (avgLoss == 0) {
      return 100.0;
    }

    // Calculate RS (Relative Strength)
    var rs = avgGain / avgLoss;

    // Calculate RSI
    var rsi = 100 - (100 / (1 + rs));

    return rsi;
  }

  public int getPeriod() {
    return period;
  }
}
