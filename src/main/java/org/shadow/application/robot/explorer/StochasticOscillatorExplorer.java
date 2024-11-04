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

  // TODO: Make constants below configurable
  // Valid only for 1-minute timeframe
  private static final double STOCH_OVERSOLD_THRESHOLD = 20.0;
  // Valid only for 1-minute timeframe
  private static final double STOCH_OVERBOUGHT_THRESHOLD = 80.0;
  // Valid only for 1-minute timeframe
  private static final double STOCH_LONG_MEDIUM_THRESHOLD = 30.0;
  // Valid only for 1-minute timeframe
  private static final double STOCH_SHORT_MEDIUM_THRESHOLD = 70.0;
  // Valid only for 1-minute timeframe
  private static final double STOCH_LONG_MINOR_THRESHOLD = 40.0;
  // Valid only for 1-minute timeframe
  private static final double STOCH_SHORT_MINOR_THRESHOLD = 60.0;

  private final Integer severity;
  private final StochasticOscillatorIndicator stochasticIndicator;

  /**
   * Constructs a StochasticOscillatorExplorer with specified severity, period, and dPeriod.
   *
   * @param severity the severity level of momentum exploration
   * @param period the look-back period for %K calculation
   * @param dPeriod the period over which to average %K to get %D
   */
  public StochasticOscillatorExplorer(Integer severity, int period, int dPeriod) {
    this.severity = severity;
    this.stochasticIndicator = new StochasticOscillatorIndicator(period, dPeriod);
    logger.info(
        "StochasticOscillatorExplorer initialized with severity {}, period {}, dPeriod {}",
        severity,
        period,
        dPeriod);
  }

  /**
   * Evaluates whether the momentum indicates a long (buy) opportunity based on Stochastic
   * Oscillator.
   *
   * @param bars the list of {@link Bar} objects representing market data
   * @return the momentum state as a {@link BinaryIsMomentumExplorationState} value
   */
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

  /**
   * Evaluates whether the momentum indicates a short (sell) opportunity based on Stochastic
   * Oscillator.
   *
   * @param bars the list of {@link Bar} objects representing market data
   * @return the momentum state as a {@link BinaryIsMomentumExplorationState} value
   */
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
   * Returns the StochasticOscillatorIndicator used by this explorer.
   *
   * @return the StochasticOscillatorIndicator
   */
  @Override
  public Indicator getIndicator() {
    return stochasticIndicator;
  }

  private BinaryIsMomentumExplorationState evaluateLongState(double percentK, double percentD) {
    if (percentK < STOCH_OVERSOLD_THRESHOLD && percentK > percentD) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (percentK < STOCH_LONG_MEDIUM_THRESHOLD && percentK > percentD) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (percentK < STOCH_LONG_MINOR_THRESHOLD && percentK > percentD) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }

  private BinaryIsMomentumExplorationState evaluateShortState(double percentK, double percentD) {
    if (percentK > STOCH_OVERBOUGHT_THRESHOLD && percentK < percentD) {
      return BinaryIsMomentumExplorationState.MAJOR;
    } else if (percentK > STOCH_SHORT_MEDIUM_THRESHOLD && percentK < percentD) {
      return BinaryIsMomentumExplorationState.MEDIUM;
    } else if (percentK > STOCH_SHORT_MINOR_THRESHOLD && percentK < percentD) {
      return BinaryIsMomentumExplorationState.MINOR;
    }
    return BinaryIsMomentumExplorationState.NOT_READY;
  }
}
