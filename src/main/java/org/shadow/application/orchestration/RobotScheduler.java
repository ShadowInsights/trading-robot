package org.shadow.application.orchestration;

import static org.shadow.application.orchestration.util.TimeUtil.calculateInitialDelayUntilNextPeriod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.application.robot.Robot;

public class RobotScheduler {

  private final Logger logger = LogManager.getLogger(RobotScheduler.class);

  private final Robot robot;
  private final TaskScheduler taskScheduler;

  public RobotScheduler(Robot robot, TaskScheduler taskScheduler) {
    this.robot = robot;
    this.taskScheduler = taskScheduler;
  }

  public void start() {
    logger.info("Starting scheduler for robot: {}", robot);

    logger.info("Initializing {} robot", robot.getSymbol());
    robot.init();
    logger.info("{} robot has been initialized", robot.getSymbol());

    var robotInterval = robot.getRobotTimeframe().interval();
    var robotUnit = robot.getRobotTimeframe().unit();

    var initialDelay = calculateInitialDelayUntilNextPeriod(robotInterval, robotUnit);
    var interval = robotUnit.toMillis(robotInterval);

    taskScheduler.start(
        () -> {
          logger.info("Executing task for robot: {}", robot);
          robot.run();
        },
        initialDelay,
        interval);
    logger.info("Scheduler started.");
  }

  public void stop() {
    logger.info("Stopping scheduler for robot: {}", robot);
    taskScheduler.stop();
    robot.stop();
    logger.info("Scheduler stopped.");
  }
}
