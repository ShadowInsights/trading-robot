package org.shadow.application.robot;

public class InSinglePositionHandler implements RobotPositionHandler<SinglePositionRobot> {

  @Override
  public void handle(SinglePositionRobot robot) {
    robot.getLogger().info("Checking if it's time to close position");
    if (robot
        .getStrategy()
        .isTimeToClosePositionInAdvance(robot.getBars(), robot.getPositions().getFirst())) {
      robot.getExchangeOrderClient().closeOrder(robot.getOrder());
      robot.getLogger().info("Closed order: {}", robot.getOrder());
      robot.setPosition(null);
      robot.setOrder(null);
      robot.setRobotPositionState(RobotPositionState.EXPLORING);
    }
  }
}
