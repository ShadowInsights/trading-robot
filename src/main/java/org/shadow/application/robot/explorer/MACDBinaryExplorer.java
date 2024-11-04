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

  private final Integer severity;
  private final MACDIndicator macdIndicator;

  private final double histogramMajorThreshold;
  private final double histogramMediumThreshold;
  private final double histogramMinorThreshold;

  /**
   * Constructs a MACDBinaryExplorer with specified parameters.
   *
   * @param severity the severity level of momentum exploration (affects the thresholds)
   * @param shortPeriod the period for the short-term EMA
   * @param longPeriod the period for the long-term EMA
   * @param signalPeriod the period for the signal line EMA
   * @param histogramMajorThreshold threshold for major signals
   * @param histogramMediumThreshold threshold for medium signals
   * @param histogramMinorThreshold threshold for minor signals
   */
  public MACDBinaryExplorer(
      Integer severity,
      int shortPeriod,
      int longPeriod,
      int signalPeriod,
      double histogramMajorThreshold,
      double histogramMediumThreshold,
      double histogramMinorThreshold) {

    this.severity = severity;
    this.macdIndicator = new MACDIndicator(shortPeriod, longPeriod, signalPeriod);

    this.histogramMajorThreshold = histogramMajorThreshold;
    this.histogramMediumThreshold = histogramMediumThreshold;
    this.histogramMinorThreshold = histogramMinorThreshold;

    logger.info(
        "MACDBinaryExplorer initialized with severity {}, shortPeriod {}, longPeriod {}, signalPeriod {}, thresholds: major={}, medium={}, minor={}",
        severity,
        shortPeriod,
        longPeriod,
        signalPeriod,
        histogramMajorThreshold,
        histogramMediumThreshold,
        histogramMinorThreshold);
  }

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

  @Override
  public Integer getSeverity() {
    return severity;
  }

  @Override
  public Indicator getIndicator() {
    return macdIndicator;
  }

  private double[] extractClosingPrices(List<Bar> bars) {
    return bars.stream().map(Bar::close).mapToDouble(BigDecimal::doubleValue).toArray();
  }

  private BinaryIsMomentumExplorationState evaluateLongState(double histogram) {
    if (histogram > histogramMajorThreshold) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (histogram > histogramMediumThreshold) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (histogram > histogramMinorThreshold) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }

  private BinaryIsMomentumExplorationState evaluateShortState(double histogram) {
    if (histogram < -histogramMajorThreshold) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (histogram < -histogramMediumThreshold) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (histogram < -histogramMinorThreshold) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }
}
