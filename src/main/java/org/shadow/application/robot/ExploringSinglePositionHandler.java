package org.shadow.application.robot;

import java.util.List;
import java.util.Optional;
import org.shadow.application.robot.common.model.Position;
import org.shadow.application.robot.common.model.PositionType;
import org.shadow.application.robot.strategy.model.BinaryPositionMomentum;
import org.shadow.application.robot.strategy.model.BinaryPositionMomentumActionType;

public class ExploringSinglePositionHandler implements RobotPositionHandler<SinglePositionRobot> {

  @Override
  public void handle(SinglePositionRobot robot) {
    var momentum = robot.getStrategy().calculatePositionMomentum(robot.getBars());
    robot.getLogger().debug("Calculated momentum: {}", momentum);

    switch (momentum.momentumActionType()) {
      case BinaryPositionMomentumActionType.DO_NOTHING -> robot.getLogger().info("Doing nothing");
      case BinaryPositionMomentumActionType.LONG -> handleLongMomentum(robot, momentum);
      case BinaryPositionMomentumActionType.SHORT -> handleShortMomentum(robot, momentum);
    }
  }

  private void handleLongMomentum(SinglePositionRobot robot, BinaryPositionMomentum momentum) {
    if (momentum.stopLoss().isPresent()) {
      var positionStopLoss = momentum.stopLoss().get();
      var percentageFromDeposit = robot.getPercentageFromDeposit();
      var orderFuturesMultiplier = robot.getOrderFuturesMultiplier();
      // TODO: Order should contain take entry price and takeProfits, finish when
      //  BinaryPositionMomentum is completed for production ready implementation
      var longOrder =
          robot
              .getExchangeOrderClient()
              .openLongOrder(
                  robot.getSymbol(),
                  null,
                  null,
                  positionStopLoss,
                  percentageFromDeposit,
                  orderFuturesMultiplier);
      var position =
          new Position(
              PositionType.LONG, longOrder.entry(), List.of(), Optional.of(positionStopLoss));
      robot.setPosition(position);
      robot.setOrder(longOrder);
      robot.setRobotPositionState(RobotPositionState.IN_POSITION);
      robot.getLogger().info("Opened long order: {}", longOrder);
    } else {
      robot.getLogger().warn("Received empty stopLoss position for LONG");
    }
  }

  private void handleShortMomentum(SinglePositionRobot robot, BinaryPositionMomentum momentum) {
    if (momentum.stopLoss().isPresent()) {
      var positionStopLoss = momentum.stopLoss().get();
      var percentageFromDeposit = robot.getPercentageFromDeposit();
      var orderFuturesMultiplier = robot.getOrderFuturesMultiplier();
      // TODO: Order should contain take entry price and takeProfits, finish when
      //  BinaryPositionMomentum is completed for production ready implementation
      var shortOrder =
          robot
              .getExchangeOrderClient()
              .openShortOrder(
                  robot.getSymbol(),
                  null,
                  null,
                  positionStopLoss,
                  percentageFromDeposit,
                  orderFuturesMultiplier);
      var position =
          new Position(
              PositionType.SHORT, shortOrder.entry(), List.of(), Optional.of(positionStopLoss));
      robot.setPosition(position);
      robot.setOrder(shortOrder);
      robot.setRobotPositionState(RobotPositionState.IN_POSITION);
      robot.getLogger().info("Opened short order: {}", shortOrder);
    } else {
      robot.getLogger().warn("Received empty stopLoss position for SHORT");
    }
  }
}
