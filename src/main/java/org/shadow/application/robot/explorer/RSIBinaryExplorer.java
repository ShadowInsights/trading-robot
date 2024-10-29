package org.shadow.application.robot.explorer;

import java.math.BigDecimal;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;
import org.shadow.application.robot.indicator.Indicator;
import org.shadow.application.robot.indicator.RSIIndicator;

/**
 * This class represents an implementation of a binary momentum explorer using the RSI (Relative
 * Strength Index) indicator. It evaluates whether the market is in a momentum phase to either go
 * long or short based on the RSI value.
 */
public class RSIBinaryExplorer implements BinaryExplorer {
  private final Logger logger = LogManager.getLogger(RSIBinaryExplorer.class);

  // TODO: Make constants below configurable
  // Valid only for 1-minute timeframe
  private static final double RSI_OVERSOLD_THRESHOLD = 25.0;
  // Valid only for 1-minute timeframe
  private static final double RSI_OVERBOUGHT_THRESHOLD = 75.0;
  // Valid only for 1-minute timeframe
  private static final double RSI_LONG_MEDIUM_THRESHOLD = 35.0;
  // Valid only for 1-minute timeframe
  private static final double RSI_SHORT_MEDIUM_THRESHOLD = 65.0;
  // Valid only for 1-minute timeframe
  private static final double RSI_LONG_MINOR_THRESHOLD = 45.0;
  // Valid only for 1-minute timeframe
  private static final double RSI_SHORT_MINOR_THRESHOLD = 55.0;

  private final Integer severity;
  private final RSIIndicator rsiIndicator;

  /**
   * Constructs an RSIBinaryExplorer with a specified severity and RSI period.
   *
   * @param severity the severity level of momentum exploration (affects the thresholds)
   * @param period the period for the RSI calculation
   */
  public RSIBinaryExplorer(Integer severity, Integer period) {
    this.severity = severity;
    this.rsiIndicator = new RSIIndicator(period);
    logger.info("RSIBinaryExplorer initialized with severity {} and period {}", severity, period);
  }

  /**
   * Evaluates whether the momentum indicates a long (buy) opportunity based on RSI values.
   *
   * @param bars the list of {@link Bar} objects representing market data
   * @return the momentum state as a {@link BinaryIsMomentumExplorationState} value
   */
  @Override
  public BinaryIsMomentumExplorationState isMomentumToLong(List<Bar> bars) {
    if (bars == null || bars.size() < rsiIndicator.getPeriod() + 1) {
      logger.warn("Bars list is null or has insufficient data");
      return BinaryIsMomentumExplorationState.NOT_READY;
    }

    var prices = extractClosingPrices(bars);
    var rsi = rsiIndicator.calculate(prices);

    var longState = evaluateLongState(rsi);

    logger.info("Long state: {}, rsi: {}", longState, rsi);

    return longState;
  }

  /**
   * Evaluates whether the momentum indicates a short (sell) opportunity based on RSI values.
   *
   * @param bars the list of {@link Bar} objects representing market data
   * @return the momentum state as a {@link BinaryIsMomentumExplorationState} value
   */
  @Override
  public BinaryIsMomentumExplorationState isMomentumToShort(List<Bar> bars) {
    if (bars == null || bars.size() < rsiIndicator.getPeriod() + 1) {
      logger.warn("Bars list is null or has insufficient data");
      return BinaryIsMomentumExplorationState.NOT_READY;
    }

    var prices = extractClosingPrices(bars);
    var rsi = rsiIndicator.calculate(prices);

    logger.info("RSI: {}", rsi);

    var shortState = evaluateShortState(rsi);

    logger.info("Short state: {}, rsi: {}", shortState, rsi);

    return shortState;
  }

  /**
   * Returns the severity level of the momentum exploration.
   *
   * @return the severity level
   */
  @Override
  public Integer getSeverity() {
    return severity;
  }

  /**
   * Returns the RSIIndicator used by this RSIBinaryExplorer.
   *
   * @return the RSIIndicator
   */
  @Override
  public Indicator getIndicator() {
    return rsiIndicator;
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
