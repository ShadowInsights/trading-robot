package org.shadow.application.robot.explorer;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;
import org.shadow.application.robot.indicator.Indicator;
import org.shadow.application.robot.indicator.StochasticOscillatorIndicator;

/**
 * This class represents an implementation of a binary momentum explorer using the Stochastic
 * Oscillator indicator. It evaluates whether the market is in a momentum phase to either go long or
 * short based on %K and %D values.
 */
public class StochasticOscillatorExplorer implements BinaryExplorer {

  private final Logger logger = LogManager.getLogger(StochasticOscillatorExplorer.class);

  private final Integer severity;
  private final StochasticOscillatorIndicator stochasticIndicator;

  private final double oversoldThreshold;
  private final double overboughtThreshold;
  private final double longMediumThreshold;
  private final double shortMediumThreshold;
  private final double longMinorThreshold;
  private final double shortMinorThreshold;

  /**
   * Constructs a StochasticOscillatorExplorer with specified parameters.
   *
   * @param severity the severity level of momentum exploration
   * @param period the look-back period for %K calculation
   * @param dPeriod the period over which to average %K to get %D
   * @param oversoldThreshold threshold indicating oversold condition
   * @param overboughtThreshold threshold indicating overbought condition
   * @param longMediumThreshold threshold for medium long signals
   * @param shortMediumThreshold threshold for medium short signals
   * @param longMinorThreshold threshold for minor long signals
   * @param shortMinorThreshold threshold for minor short signals
   */
  public StochasticOscillatorExplorer(
      Integer severity,
      int period,
      int dPeriod,
      double oversoldThreshold,
      double overboughtThreshold,
      double longMediumThreshold,
      double shortMediumThreshold,
      double longMinorThreshold,
      double shortMinorThreshold) {

    this.severity = severity;
    this.stochasticIndicator = new StochasticOscillatorIndicator(period, dPeriod);

    this.oversoldThreshold = oversoldThreshold;
    this.overboughtThreshold = overboughtThreshold;
    this.longMediumThreshold = longMediumThreshold;
    this.shortMediumThreshold = shortMediumThreshold;
    this.longMinorThreshold = longMinorThreshold;
    this.shortMinorThreshold = shortMinorThreshold;

    logger.info(
        "StochasticOscillatorExplorer initialized with severity {}, period {}, dPeriod {}, thresholds: oversold={}, overbought={}, longMedium={}, shortMedium={}, longMinor={}, shortMinor={}",
        severity,
        period,
        dPeriod,
        oversoldThreshold,
        overboughtThreshold,
        longMediumThreshold,
        shortMediumThreshold,
        longMinorThreshold,
        shortMinorThreshold);
  }

  @Override
  public BinaryIsMomentumExplorationState isMomentumToLong(List<Bar> bars) {
    if (bars == null || bars.size() < stochasticIndicator.getRequiredPeriodThreshold()) {
      logger.warn("Bars list is null or has insufficient data");
      return BinaryIsMomentumExplorationState.NOT_READY;
    }

    var result = stochasticIndicator.calculate(bars);

    var percentK = result.percentK();
    var percentD = result.percentD();

    var longState = evaluateLongState(percentK, percentD);

    logger.info("Long state: {}, %K: {}, %D: {}", longState, percentK, percentD);

    return longState;
  }

  @Override
  public BinaryIsMomentumExplorationState isMomentumToShort(List<Bar> bars) {
    if (bars == null || bars.size() < stochasticIndicator.getRequiredPeriodThreshold()) {
      logger.warn("Bars list is null or has insufficient data");
      return BinaryIsMomentumExplorationState.NOT_READY;
    }

    var result = stochasticIndicator.calculate(bars);

    var percentK = result.percentK();
    var percentD = result.percentD();

    var shortState = evaluateShortState(percentK, percentD);

    logger.info("Short state: {}, %K: {}, %D: {}", shortState, percentK, percentD);

    return shortState;
  }

  @Override
  public Integer getSeverity() {
    return severity;
  }

  @Override
  public Indicator getIndicator() {
    return stochasticIndicator;
  }

  private BinaryIsMomentumExplorationState evaluateLongState(double percentK, double percentD) {
    if (percentK < oversoldThreshold && percentK > percentD) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (percentK < longMediumThreshold && percentK > percentD) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (percentK < longMinorThreshold && percentK > percentD) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }

  private BinaryIsMomentumExplorationState evaluateShortState(double percentK, double percentD) {
    if (percentK > overboughtThreshold && percentK < percentD) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (percentK > shortMediumThreshold && percentK < percentD) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (percentK > shortMinorThreshold && percentK < percentD) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }
}
