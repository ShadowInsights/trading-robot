package org.shadow.application.robot.strategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.application.robot.blocker.Blocker;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.common.model.Position;
import org.shadow.application.robot.common.model.PositionType;
import org.shadow.application.robot.explorer.BinaryExplorer;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;
import org.shadow.application.robot.strategy.model.BinaryPositionMomentum;
import org.shadow.application.robot.strategy.model.BinaryPositionMomentumActionType;

public class BinaryStrategy implements Strategy<BinaryPositionMomentum> {

  private static final Logger logger = LogManager.getLogger(BinaryStrategy.class);

  private final List<BinaryExplorer> binaryExplorers;
  private final List<Blocker> blockers;
  private final BigDecimal stopLossRequiredPercentage;
  private final Map<BinaryIsMomentumExplorationState, Integer>
      binaryIsMomentumExplorationStateIntegerMultiplierMap;

  public BinaryStrategy(
      List<BinaryExplorer> binaryExplorers,
      List<Blocker> blockers,
      BigDecimal stopLossRequiredPercentage,
      Map<BinaryIsMomentumExplorationState, Integer>
          binaryIsMomentumExplorationStateIntegerMultiplierMap) {
    this.binaryExplorers = binaryExplorers;
    this.blockers = blockers;
    this.stopLossRequiredPercentage = stopLossRequiredPercentage;
    this.binaryIsMomentumExplorationStateIntegerMultiplierMap =
        binaryIsMomentumExplorationStateIntegerMultiplierMap;
  }

  @Override
  public BinaryPositionMomentum calculatePositionMomentum(List<Bar> bars) {
    logger.debug("Starting position momentum calculation with {} bars", bars.size());

    if (shouldBlock(bars)) {
      logger.info("Blocking detected; returning DO_NOTHING action");
      return new BinaryPositionMomentum(
          BinaryPositionMomentumActionType.DO_NOTHING, Optional.empty());
    }

    var severityDifference = calculateSeverityDifference(bars);
    logger.debug("Calculated severity difference: {}", severityDifference);

    if (severityDifference > 0) {
      var stopLoss = calculateStopLoss(PositionType.LONG, bars);
      logger.info("Long position detected. Stop loss: {}", stopLoss);
      return new BinaryPositionMomentum(
          BinaryPositionMomentumActionType.LONG, Optional.of(stopLoss));
    } else if (severityDifference < 0) {
      var stopLoss = calculateStopLoss(PositionType.SHORT, bars);
      logger.info("Short position detected. Stop loss: {}", stopLoss);
      return new BinaryPositionMomentum(
          BinaryPositionMomentumActionType.SHORT, Optional.of(stopLoss));
    }

    logger.info("No action required. Returning DO_NOTHING");
    return new BinaryPositionMomentum(
        BinaryPositionMomentumActionType.DO_NOTHING, Optional.empty());
  }

  @Override
  public boolean isTimeToClosePositionInAdvance(List<Bar> bars, Position position) {
    logger.debug("Checking if it's time to close position in advance. Position: {}", position);

    if (shouldBlock(bars)) {
      logger.info("Blocking detected; position should be closed in advance");
      return true;
    }

    var severityDifference = calculateSeverityDifference(bars);
    logger.debug("Calculated severity difference: {}", severityDifference);

    if (position.type() == PositionType.LONG && severityDifference < 0) {
      logger.info("Long position and severity difference is negative; close in advance");
      return true;
    } else if (position.type() == PositionType.SHORT && severityDifference > 0) {
      logger.info("Short position and severity difference is positive; close in advance");
      return true;
    }

    return false;
  }

  private boolean shouldBlock(List<Bar> bars) {
    logger.debug("Checking if should block based on {} bars", bars.size());
    // TODO: Calculation should be done in parallel in case of speed problem
    for (var blocker : blockers) {
      if (blocker.isMomentumToBlocking(bars)) {
        logger.info("Blocker detected that blocks momentum");
        return true;
      }
    }
    return false;
  }

  private int calculateSeverityDifference(List<Bar> bars) {
    logger.debug("Calculating severity difference with {} bars", bars.size());
    var longVotingPower = 0;
    var shortVotingPower = 0;

    // TODO: Calculation should be done in parallel in case of speed problem
    for (var explorer : binaryExplorers) {
      var isMomentumToLongState = explorer.isMomentumToLong(bars);
      if (!isMomentumToLongState.equals(BinaryIsMomentumExplorationState.NOT_READY)) {
        int longMultiplier =
            binaryIsMomentumExplorationStateIntegerMultiplierMap.get(isMomentumToLongState);
        longVotingPower += explorer.getSeverity() * longMultiplier;
        logger.debug(
            "Long voting power updated: {} (Multiplier: {})", longVotingPower, longMultiplier);
      }
      var isMomentumToShortState = explorer.isMomentumToShort(bars);
      if (!isMomentumToShortState.equals(BinaryIsMomentumExplorationState.NOT_READY)) {
        int shortMultiplier =
            binaryIsMomentumExplorationStateIntegerMultiplierMap.get(isMomentumToShortState);
        shortVotingPower += explorer.getSeverity() * shortMultiplier;
        logger.debug(
            "Short voting power updated: {} (Multiplier: {})", shortVotingPower, shortMultiplier);
      }
    }

    int severityDifference = longVotingPower - shortVotingPower;
    logger.debug("Calculated severity difference: {}", severityDifference);
    return severityDifference;
  }

  private BigDecimal calculateStopLoss(PositionType positionType, List<Bar> bars) {
    var latestClosePrice = bars.get(bars.size() - 1).close();
    var stopLossAdjustment = latestClosePrice.multiply(stopLossRequiredPercentage);
    BigDecimal stopLoss;

    if (positionType == PositionType.LONG) {
      stopLoss = latestClosePrice.subtract(stopLossAdjustment);
      logger.debug("Calculated stop loss for LONG position: {}", stopLoss);
    } else {
      stopLoss = latestClosePrice.add(stopLossAdjustment);
      logger.debug("Calculated stop loss for SHORT position: {}", stopLoss);
    }
    return stopLoss;
  }

  @Override
  public List<BinaryExplorer> getBinaryExplorers() {
    return binaryExplorers;
  }

  @Override
  public List<Blocker> getBlockers() {
    return blockers;
  }

  @Override
  public BigDecimal getStopLossRequiredPercentage() {
    return stopLossRequiredPercentage;
  }

  @Override
  public Map<BinaryIsMomentumExplorationState, Integer>
      getBinaryIsMomentumExplorationStateIntegerMultiplierMap() {
    return binaryIsMomentumExplorationStateIntegerMultiplierMap;
  }
}
