package org.shadow.application.robot.indicator;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.indicator.exception.InsufficientDataException;
import org.shadow.application.robot.indicator.model.StochasticOscillatorResult;

/**
 * The StochasticOscillatorIndicator calculates the Stochastic Oscillator for a given set of bars.
 * It consists of %K and %D lines, which are used to identify overbought or oversold conditions.
 */
public class StochasticOscillatorIndicator implements Indicator {

  private final int period;
  private final int dPeriod;

  /**
   * Constructs a StochasticOscillatorIndicator with specified period and dPeriod.
   *
   * @param period the look-back period for %K calculation
   * @param dPeriod the period over which to average %K to get %D
   */
  public StochasticOscillatorIndicator(int period, int dPeriod) {
    if (period <= 0 || dPeriod <= 0) {
      throw new IllegalArgumentException("Period and dPeriod must be positive integers.");
    }
    this.period = period;
    this.dPeriod = dPeriod;
  }

  /**
   * Calculates the Stochastic Oscillator (%K and %D) for the given list of bars.
   *
   * @param bars the list of {@link Bar} objects representing market data
   * @return a {@link StochasticOscillatorResult} containing %K and %D values
   * @throws InsufficientDataException if there is not enough data to perform the calculation
   */
  public StochasticOscillatorResult calculate(List<Bar> bars) {
    if (bars == null || bars.size() < getRequiredPeriodThreshold()) {
      throw new InsufficientDataException("Not enough data to calculate Stochastic Oscillator.");
    }

    var kValues = new double[dPeriod];

    for (var i = 0; i < dPeriod; i++) {
      var startIndex = bars.size() - period - i;
      var endIndex = bars.size() - i;

      var periodBars = bars.subList(startIndex, endIndex);
      var highestHigh =
          periodBars.stream()
              .map(Bar::high)
              .mapToDouble(BigDecimal::doubleValue)
              .max()
              .orElseThrow();
      var lowestLow =
          periodBars.stream()
              .map(Bar::low)
              .mapToDouble(BigDecimal::doubleValue)
              .min()
              .orElseThrow();

      var currentClose = bars.get(bars.size() - 1 - i).close().doubleValue();

      var percentK = ((currentClose - lowestLow) / (highestHigh - lowestLow)) * 100.0;
      kValues[dPeriod - 1 - i] = percentK;
    }

    var sumK = Arrays.stream(kValues).sum();
    var percentD = sumK / dPeriod;

    var percentK = kValues[kValues.length - 1];

    return new StochasticOscillatorResult(percentK, percentD);
  }

  @Override
  public int getPeriod() {
    return period;
  }

  @Override
  public int getRequiredPeriodThreshold() {
    return period + dPeriod - 1;
  }
}
