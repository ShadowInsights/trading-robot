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

  private final Integer severity;
  private final RSIIndicator rsiIndicator;

  private final double oversoldThreshold;
  private final double overboughtThreshold;
  private final double longMediumThreshold;
  private final double shortMediumThreshold;
  private final double longMinorThreshold;
  private final double shortMinorThreshold;

  /**
   * Constructs an RSIBinaryExplorer with specified parameters.
   *
   * @param severity the severity level of momentum exploration (affects the thresholds)
   * @param period the period for the RSI calculation
   * @param oversoldThreshold RSI value indicating oversold condition
   * @param overboughtThreshold RSI value indicating overbought condition
   * @param longMediumThreshold threshold for medium long signals
   * @param shortMediumThreshold threshold for medium short signals
   * @param longMinorThreshold threshold for minor long signals
   * @param shortMinorThreshold threshold for minor short signals
   */
  public RSIBinaryExplorer(
      Integer severity,
      Integer period,
      double oversoldThreshold,
      double overboughtThreshold,
      double longMediumThreshold,
      double shortMediumThreshold,
      double longMinorThreshold,
      double shortMinorThreshold) {

    this.severity = severity;
    this.rsiIndicator = new RSIIndicator(period);

    this.oversoldThreshold = oversoldThreshold;
    this.overboughtThreshold = overboughtThreshold;
    this.longMediumThreshold = longMediumThreshold;
    this.shortMediumThreshold = shortMediumThreshold;
    this.longMinorThreshold = longMinorThreshold;
    this.shortMinorThreshold = shortMinorThreshold;

    logger.info(
        "RSIBinaryExplorer initialized with severity {}, period {}, thresholds: oversold={}, overbought={}, longMedium={}, shortMedium={}, longMinor={}, shortMinor={}",
        severity,
        period,
        oversoldThreshold,
        overboughtThreshold,
        longMediumThreshold,
        shortMediumThreshold,
        longMinorThreshold,
        shortMinorThreshold);
  }

  @Override
  public BinaryIsMomentumExplorationState isMomentumToLong(List<Bar> bars) {
    if (bars == null || bars.size() < rsiIndicator.getPeriod() + 1) {
      logger.warn("Bars list is null or has insufficient data");
      return BinaryIsMomentumExplorationState.NOT_READY;
    }

    var prices = extractClosingPrices(bars);
    var rsi = rsiIndicator.calculate(prices);

    var longState = evaluateLongState(rsi);

    logger.info("Long state: {}, RSI: {}", longState, rsi);

    return longState;
  }

  @Override
  public BinaryIsMomentumExplorationState isMomentumToShort(List<Bar> bars) {
    if (bars == null || bars.size() < rsiIndicator.getPeriod() + 1) {
      logger.warn("Bars list is null or has insufficient data");
      return BinaryIsMomentumExplorationState.NOT_READY;
    }

    var prices = extractClosingPrices(bars);
    var rsi = rsiIndicator.calculate(prices);

    var shortState = evaluateShortState(rsi);

    logger.info("Short state: {}, RSI: {}", shortState, rsi);

    return shortState;
  }

  @Override
  public Integer getSeverity() {
    return severity;
  }

  @Override
  public Indicator getIndicator() {
    return rsiIndicator;
  }

  private double[] extractClosingPrices(List<Bar> bars) {
    return bars.stream().map(Bar::close).mapToDouble(BigDecimal::doubleValue).toArray();
  }

  private BinaryIsMomentumExplorationState evaluateLongState(double rsi) {
    if (rsi < oversoldThreshold) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (rsi < longMediumThreshold) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (rsi < longMinorThreshold) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }

  private BinaryIsMomentumExplorationState evaluateShortState(double rsi) {
    if (rsi > overboughtThreshold) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (rsi > shortMediumThreshold) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (rsi > shortMinorThreshold) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }
}
