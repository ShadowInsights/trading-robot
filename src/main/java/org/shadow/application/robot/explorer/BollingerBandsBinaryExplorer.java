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

  private final Integer severity;
  private final BollingerBandsIndicator bollingerBandsIndicator;

  private final double lowerBandThreshold;
  private final double upperBandThreshold;
  private final double longMediumThreshold;
  private final double shortMediumThreshold;
  private final double longMinorThreshold;
  private final double shortMinorThreshold;

  /**
   * Constructs a BollingerBandsBinaryExplorer with specified parameters.
   *
   * @param severity the severity level of momentum exploration (affects the thresholds)
   * @param period the period for the Bollinger Bands calculation
   * @param standardDeviationMultiplier the multiplier for standard deviation (usually 2)
   * @param lowerBandThreshold threshold for the lower band (e.g., 0.0)
   * @param upperBandThreshold threshold for the upper band (e.g., 1.0)
   * @param longMediumThreshold threshold for medium long signals
   * @param shortMediumThreshold threshold for medium short signals
   * @param longMinorThreshold threshold for minor long signals
   * @param shortMinorThreshold threshold for minor short signals
   */
  public BollingerBandsBinaryExplorer(
      Integer severity,
      int period,
      double standardDeviationMultiplier,
      double lowerBandThreshold,
      double upperBandThreshold,
      double longMediumThreshold,
      double shortMediumThreshold,
      double longMinorThreshold,
      double shortMinorThreshold) {

    this.severity = severity;
    this.bollingerBandsIndicator = new BollingerBandsIndicator(period, standardDeviationMultiplier);

    this.lowerBandThreshold = lowerBandThreshold;
    this.upperBandThreshold = upperBandThreshold;
    this.longMediumThreshold = longMediumThreshold;
    this.shortMediumThreshold = shortMediumThreshold;
    this.longMinorThreshold = longMinorThreshold;
    this.shortMinorThreshold = shortMinorThreshold;

    logger.info(
        "BollingerBandsBinaryExplorer initialized with severity {}, period {}, standardDeviationMultiplier {}, thresholds: lowerBand={}, upperBand={}, longMedium={}, shortMedium={}, longMinor={}, shortMinor={}",
        severity,
        period,
        standardDeviationMultiplier,
        lowerBandThreshold,
        upperBandThreshold,
        longMediumThreshold,
        shortMediumThreshold,
        longMinorThreshold,
        shortMinorThreshold);
  }

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

  @Override
  public Integer getSeverity() {
    return severity;
  }

  @Override
  public Indicator getIndicator() {
    return bollingerBandsIndicator;
  }

  private double[] extractClosingPrices(List<Bar> bars) {
    return bars.stream().map(Bar::close).mapToDouble(BigDecimal::doubleValue).toArray();
  }

  private double calculatePositionInBands(double price, BollingerBandsResult bands) {
    var lowerBand = bands.lowerBand();
    var upperBand = bands.upperBand();
    return (price - lowerBand) / (upperBand - lowerBand);
  }

  private BinaryIsMomentumExplorationState evaluateLongState(double position) {
    if (position <= lowerBandThreshold) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (position <= longMediumThreshold) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (position <= longMinorThreshold) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }

  private BinaryIsMomentumExplorationState evaluateShortState(double position) {
    if (position >= upperBandThreshold) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (position >= shortMediumThreshold) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (position >= shortMinorThreshold) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }
}
