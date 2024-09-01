package org.shadow.application.robot.strategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.shadow.application.robot.blocker.Blocker;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.common.model.Position;
import org.shadow.application.robot.common.model.PositionType;
import org.shadow.application.robot.explorer.BinaryExplorer;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;
import org.shadow.application.robot.strategy.model.BinaryPositionMomentum;
import org.shadow.application.robot.strategy.model.BinaryPositionMomentumActionType;

public class BinaryStrategy implements Strategy<BinaryPositionMomentum> {

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
    if (shouldBlock(bars)) {
      return new BinaryPositionMomentum(
          BinaryPositionMomentumActionType.DO_NOTHING, Optional.empty());
    }

    var severityDifference = calculateSeverityDifference(bars);

    if (severityDifference > 0) {
      var stopLoss = calculateStopLoss(PositionType.LONG, bars);
      return new BinaryPositionMomentum(
          BinaryPositionMomentumActionType.LONG, Optional.of(stopLoss));
    } else if (severityDifference < 0) {
      var stopLoss = calculateStopLoss(PositionType.SHORT, bars);
      return new BinaryPositionMomentum(
          BinaryPositionMomentumActionType.SHORT, Optional.of(stopLoss));
    }

    return new BinaryPositionMomentum(
        BinaryPositionMomentumActionType.DO_NOTHING, Optional.empty());
  }

  @Override
  public boolean isTimeToClosePositionInAdvance(List<Bar> bars, Position position) {
    if (shouldBlock(bars)) {
      return true;
    }

    var severityDifference = calculateSeverityDifference(bars);

    if (position.type() == PositionType.LONG && severityDifference < 0) {
      return true;
    } else return position.type() == PositionType.SHORT && severityDifference > 0;
  }

  private boolean shouldBlock(List<Bar> bars) {
    // TODO: Calculation should be done in parallel in case of speed problem
    for (var blocker : blockers) {
      if (blocker.isMomentumToBlocking(bars)) {
        return true;
      }
    }
    return false;
  }

  private int calculateSeverityDifference(List<Bar> bars) {
    var longVotingPower = 0;
    var shortVotingPower = 0;

    // TODO: Calculation should be done in parallel in case of speed problem
    for (var explorer : binaryExplorers) {
      var isMomentumToLongState = explorer.isMomentumToLong(bars);
      if (!isMomentumToLongState.equals(BinaryIsMomentumExplorationState.NOT_READY)) {
        longVotingPower +=
            explorer.getSeverity()
                * binaryIsMomentumExplorationStateIntegerMultiplierMap.get(isMomentumToLongState);
      }
      var isMomentumToShortState = explorer.isMomentumToShort(bars);
      if (!isMomentumToShortState.equals(BinaryIsMomentumExplorationState.NOT_READY)) {
        shortVotingPower +=
            explorer.getSeverity()
                * binaryIsMomentumExplorationStateIntegerMultiplierMap.get(isMomentumToLongState);
      }
    }

    return longVotingPower - shortVotingPower;
  }

  private BigDecimal calculateStopLoss(PositionType positionType, List<Bar> bars) {
    var latestClosePrice = bars.getLast().close();
    var stopLossAdjustment = latestClosePrice.multiply(stopLossRequiredPercentage);

    if (positionType == PositionType.LONG) {
      return latestClosePrice.subtract(stopLossAdjustment);
    } else {
      return latestClosePrice.add(stopLossAdjustment);
    }
  }
}
