package org.shadow.application.robot.explorer;

import java.math.BigDecimal;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;
import org.shadow.application.robot.indicator.BollingerBandsIndicator;
import org.shadow.application.robot.indicator.Indicator;
import org.shadow.application.robot.indicator.model.BollingerBandsResult;

/**
 * This class represents an implementation of a binary momentum explorer using the Bollinger Bands
 * indicator. It evaluates whether the market is in a momentum phase to either go long or short
 * based on the price's position relative to the Bollinger Bands.
 */
public class BollingerBandsBinaryExplorer implements BinaryExplorer {

  private final Logger logger = LogManager.getLogger(BollingerBandsBinaryExplorer.class);

  // TODO: Make constants below configurable
  // Valid only for 1-minute timeframe
  private static final double BB_LOWER_BAND_THRESHOLD = 0.0;
  // Valid only for 1-minute timeframe
  private static final double BB_UPPER_BAND_THRESHOLD = 1.0;
  // Valid only for 1-minute timeframe
  private static final double BB_LONG_MEDIUM_THRESHOLD = 0.2;
  // Valid only for 1-minute timeframe
  private static final double BB_SHORT_MEDIUM_THRESHOLD = 0.8;
  // Valid only for 1-minute timeframe
  private static final double BB_LONG_MINOR_THRESHOLD = 0.4;
  // Valid only for 1-minute timeframe
  private static final double BB_SHORT_MINOR_THRESHOLD = 0.6;

  private final Integer severity;
  private final BollingerBandsIndicator bollingerBandsIndicator;

  /**
   * Constructs a BollingerBandsBinaryExplorer with specified severity, period, and standard
   * deviation multiplier.
   *
   * @param severity the severity level of momentum exploration (affects the thresholds)
   * @param period the period for the Bollinger Bands calculation
   * @param standardDeviationMultiplier the multiplier for standard deviation (usually 2)
   */
  public BollingerBandsBinaryExplorer(
      Integer severity, int period, double standardDeviationMultiplier) {
    this.severity = severity;
    this.bollingerBandsIndicator = new BollingerBandsIndicator(period, standardDeviationMultiplier);
    logger.info(
        "BollingerBandsBinaryExplorer initialized with severity {}, period {}, standardDeviationMultiplier {}",
        severity,
        period,
        standardDeviationMultiplier);
  }

  /**
   * Evaluates whether the momentum indicates a long (buy) opportunity based on Bollinger Bands.
   *
   * @param bars the list of {@link Bar} objects representing market data
   * @return the momentum state as a {@link BinaryIsMomentumExplorationState} value
   */
  @Override
  public BinaryIsMomentumExplorationState isMomentumToLong(List<Bar> bars) {
    if (bars == null || bars.size() < bollingerBandsIndicator.getRequiredPeriodThreshold()) {
      logger.warn("Bars list is null or has insufficient data");
      return BinaryIsMomentumExplorationState.NOT_READY;
    }

    var prices = extractClosingPrices(bars);
    var bollingerResult = bollingerBandsIndicator.calculate(prices);

    var currentPrice = prices[prices.length - 1];
    var position = calculatePositionInBands(currentPrice, bollingerResult);

    var longState = evaluateLongState(position);

    logger.info("Long state: {}, position in bands: {}", longState, position);

    return longState;
  }

  /**
   * Evaluates whether the momentum indicates a short (sell) opportunity based on Bollinger Bands.
   *
   * @param bars the list of {@link Bar} objects representing market data
   * @return the momentum state as a {@link BinaryIsMomentumExplorationState} value
   */
  @Override
  public BinaryIsMomentumExplorationState isMomentumToShort(List<Bar> bars) {
    if (bars == null || bars.size() < bollingerBandsIndicator.getRequiredPeriodThreshold()) {
      logger.warn("Bars list is null or has insufficient data");
      return BinaryIsMomentumExplorationState.NOT_READY;
    }

    var prices = extractClosingPrices(bars);
    var bollingerResult = bollingerBandsIndicator.calculate(prices);

    var currentPrice = prices[prices.length - 1];
    var position = calculatePositionInBands(currentPrice, bollingerResult);

    var shortState = evaluateShortState(position);

    logger.info("Short state: {}, position in bands: {}", shortState, position);

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
   * Returns the BollingerBandsIndicator used by this explorer.
   *
   * @return the BollingerBandsIndicator
   */
  @Override
  public Indicator getIndicator() {
    return bollingerBandsIndicator;
  }

  private double[] extractClosingPrices(List<Bar> bars) {
    return bars.stream()
        .map(Bar::close)
        .map(BigDecimal::doubleValue)
        .mapToDouble(Double::doubleValue)
        .toArray();
  }

  /**
   * Calculates the position of the current price within the Bollinger Bands. Returns a value
   * between 0 and 1, where 0 is at the lower band and 1 is at the upper band.
   */
  private double calculatePositionInBands(double price, BollingerBandsResult bands) {
    double lowerBand = bands.lowerBand();
    double upperBand = bands.upperBand();
    return (price - lowerBand) / (upperBand - lowerBand);
  }

  private BinaryIsMomentumExplorationState evaluateLongState(double position) {
    if (position <= BB_LOWER_BAND_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (position <= BB_LONG_MEDIUM_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (position <= BB_LONG_MINOR_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }

  private BinaryIsMomentumExplorationState evaluateShortState(double position) {
    if (position >= BB_UPPER_BAND_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (position >= BB_SHORT_MEDIUM_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (position >= BB_SHORT_MINOR_THRESHOLD) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }
}
