package org.shadow.application.robot.explorer;

import java.util.List;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;
import org.shadow.application.robot.indicator.Indicator;

/**
 * This interface represents a binary explorer that evaluates whether the market is in a momentum
 * phase to either go long or short based on a specific indicator.
 */
public interface BinaryExplorer {

  /**
   * Evaluates whether the market is in a momentum phase to go long based on the indicator.
   *
   * @param bars the list of bars to evaluate
   * @return the exploration state indicating whether the market is in a momentum phase to go long
   */
  BinaryIsMomentumExplorationState isMomentumToLong(List<Bar> bars);

  /**
   * Evaluates whether the market is in a momentum phase to go short based on the indicator.
   *
   * @param bars the list of bars to evaluate
   * @return the exploration state indicating whether the market is in a momentum phase to go short
   */
  BinaryIsMomentumExplorationState isMomentumToShort(List<Bar> bars);

  /**
   * Gets the severity level of momentum exploration.
   *
   * @return the severity level of momentum exploration
   */
  Integer getSeverity();

  /**
   * Gets the indicator used by the explorer.
   *
   * @return the indicator used by the explorer
   */
  Indicator getIndicator();
}
