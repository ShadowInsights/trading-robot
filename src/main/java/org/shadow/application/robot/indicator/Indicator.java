package org.shadow.application.robot.indicator;

/**
 * This interface represents an indicator that can be used by a binary explorer to evaluate whether
 * the market is in a momentum phase to either go long or short.
 */
public interface Indicator {

  /**
   * Gets the period of the indicator.
   *
   * @return the period of the indicator
   */
  int getPeriod();

  /**
   * Gets the required period threshold for the indicator.
   *
   * @return the required period threshold for the indicator
   */
  int getRequiredPeriodThreshold();
}
