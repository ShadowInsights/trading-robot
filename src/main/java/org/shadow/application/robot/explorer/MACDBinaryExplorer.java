package org.shadow.application.robot.explorer;

import java.math.BigDecimal;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;
import org.shadow.application.robot.indicator.Indicator;
import org.shadow.application.robot.indicator.MACDIndicator;

/**
 * This class represents an implementation of a binary momentum explorer using the MACD (Moving
 * Average Convergence Divergence) indicator. It evaluates whether the market is in a momentum phase
 * to either go long or short based on the MACD histogram value.
 */
public class MACDBinaryExplorer implements BinaryExplorer {
  private final Logger logger = LogManager.getLogger(MACDBinaryExplorer.class);

  // TODO: Make constants below configurable
  private static final double MACD_HISTOGRAM_MAJOR_THRESHOLD = 0.5;
  private static final double MACD_HISTOGRAM_MEDIUM_THRESHOLD = 0.2;
  private static final double MACD_HISTOGRAM_MINOR_THRESHOLD = 0.05;

  private final Integer severity;
  private final MACDIndicator macdIndicator;

  /**
   * Constructs a MACDBinaryExplorer with specified severity and MACD periods.
   *
   * @param severity the severity level of momentum exploration (affects the thresholds)
   * @param shortPeriod the period for the short-term EMA
   * @param longPeriod the period for the long-term EMA
   * @param signalPeriod the period for the signal line EMA
   */
  public MACDBinaryExplorer(Integer severity, int shortPeriod, int longPeriod, int signalPeriod) {
    this.severity = severity;
    this.macdIndicator = new MACDIndicator(shortPeriod, longPeriod, signalPeriod);
    logger.info(
        "MACDBinaryExplorer initialized with severity {}, shortPeriod {}, longPeriod {}, signalPeriod {}",
        severity,
        shortPeriod,
        longPeriod,
        signalPeriod);
  }

  /**
   * Evaluates whether the momentum indicates a long (buy) opportunity based on MACD histogram
   * values.
   *
   * @param bars the list of {@link Bar} objects representing market data
   * @return the momentum state as a {@link BinaryIsMomentumExplorationState} value
   */
  @Override
  public BinaryIsMomentumExplorationState isMomentumToLong(List<Bar> bars) {
    if (bars == null || bars.size() < macdIndicator.getRequiredPeriodThreshold()) {
      logger.warn("Bars list is null or has insufficient data");
      return BinaryIsMomentumExplorationState.NOT_READY;
    }

    var prices = extractClosingPrices(bars);
    var macdResult = macdIndicator.calculate(prices);

    var histogram = macdResult.getHistogram();

    var longState = evaluateLongState(histogram);

    logger.info("Long state: {}, histogram: {}", longState, histogram);

    return longState;
  }

  /**
   * Evaluates whether the momentum indicates a short (sell) opportunity based on MACD histogram
   * values.
   *
   * @param bars the list of {@link Bar} objects representing market data
   * @return the momentum state as a {@link BinaryIsMomentumExplorationState} value
   */
  @Override
  public BinaryIsMomentumExplorationState isMomentumToShort(List<Bar> bars) {
    if (bars == null || bars.size() < macdIndicator.getRequiredPeriodThreshold()) {
      logger.warn("Bars list is null or has insufficient data");
      return BinaryIsMomentumExplorationState.NOT_READY;
    }

    var prices = extractClosingPrices(bars);
    var macdResult = macdIndicator.calculate(prices);

    var histogram = macdResult.getHistogram();

    var shortState = evaluateShortState(histogram);

    logger.info("Short state: {}, histogram: {}", shortState, histogram);

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
   * Returns the MACDIndicator used by this MACDBinaryExplorer.
   *
   * @return the MACDIndicator
   */
  @Override
  public Indicator getIndicator() {
    return macdIndicator;
  }

  private double[] extractClosingPrices(List<Bar> bars) {
    return bars.stream()
        .map(Bar::close)
        .map(BigDecimal::doubleValue)
        .mapToDouble(Double::doubleValue)
        .toArray();
  }

  private BinaryIsMomentumExplorationState evaluateLongState(double histogram) {
    if (histogram > MACD_HISTOGRAM_MAJOR_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (histogram > MACD_HISTOGRAM_MEDIUM_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (histogram > MACD_HISTOGRAM_MINOR_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }

  private BinaryIsMomentumExplorationState evaluateShortState(double histogram) {
    if (histogram < -MACD_HISTOGRAM_MAJOR_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (histogram < -MACD_HISTOGRAM_MEDIUM_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (histogram < -MACD_HISTOGRAM_MINOR_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }
}
