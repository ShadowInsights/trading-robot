package org.shadow.application.robot.explorer;

import java.math.BigDecimal;
import java.util.List;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;
import org.shadow.application.robot.indicator.RSIIndicator;

public class RSIBinaryExplorer implements BinaryExplorer {

  // TODO: Make constants below configurable
  // Valid only for 1 minute timeframe
  private static final double RSI_OVERSOLD_THRESHOLD = 25.0;
  // Valid only for 1 minute timeframe
  private static final double RSI_OVERBOUGHT_THRESHOLD = 75.0;
  // Valid only for 1 minute timeframe
  private static final double RSI_LONG_MEDIUM_THRESHOLD = 35.0;
  // Valid only for 1 minute timeframe
  private static final double RSI_SHORT_MEDIUM_THRESHOLD = 65.0;
  // Valid only for 1 minute timeframe
  private static final double RSI_LONG_MINOR_THRESHOLD = 45.0;
  // Valid only for 1 minute timeframe
  private static final double RSI_SHORT_MINOR_THRESHOLD = 55.0;

  private final Integer severity;
  private final RSIIndicator rsiIndicator;

  public RSIBinaryExplorer(Integer severity, Integer period) {
    this.severity = severity;
    this.rsiIndicator = new RSIIndicator(period);
  }

  @Override
  public BinaryIsMomentumExplorationState isMomentumToLong(List<Bar> bars) {
    if (bars == null || bars.size() < rsiIndicator.getPeriod()) {
      return BinaryIsMomentumExplorationState.NOT_READY;
    }

    double[] prices = extractClosingPrices(bars);
    double rsi = rsiIndicator.calculate(prices);

    return evaluateLongState(rsi);
  }

  @Override
  public BinaryIsMomentumExplorationState isMomentumToShort(List<Bar> bars) {
    if (bars == null || bars.size() < rsiIndicator.getPeriod()) {
      return BinaryIsMomentumExplorationState.NOT_READY;
    }

    double[] prices = extractClosingPrices(bars);
    double rsi = rsiIndicator.calculate(prices);

    return evaluateShortState(rsi);
  }

  @Override
  public Integer getSeverity() {
    return severity;
  }

  private double[] extractClosingPrices(List<Bar> bars) {
    return bars.stream()
        .map(Bar::close)
        .map(BigDecimal::doubleValue)
        .mapToDouble(Double::doubleValue)
        .toArray();
  }

  private BinaryIsMomentumExplorationState evaluateLongState(double rsi) {
    if (rsi < RSI_OVERSOLD_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (rsi < RSI_LONG_MEDIUM_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (rsi < RSI_LONG_MINOR_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }

  private BinaryIsMomentumExplorationState evaluateShortState(double rsi) {
    if (rsi > RSI_OVERBOUGHT_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (rsi > RSI_SHORT_MEDIUM_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (rsi > RSI_SHORT_MINOR_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }
}
