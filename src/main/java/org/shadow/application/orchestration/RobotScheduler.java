package org.shadow.application.orchestration;

import static org.shadow.application.orchestration.util.TimeUtil.calculateInitialDelay;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.application.robot.Robot;

public class RobotScheduler {

  private static final Logger logger = LogManager.getLogger(RobotScheduler.class);

  private final Robot robot;
  private final TaskScheduler taskScheduler;

  public RobotScheduler(Robot robot, TaskScheduler taskScheduler) {
    this.robot = robot;
    this.taskScheduler = taskScheduler;
  }

  public void start() {
    logger.info("Starting scheduler for robot: {}", robot);

    var interval = robot.getRobotTimeframe().interval();
    var unit = robot.getRobotTimeframe().unit();
    var initialDelay = calculateInitialDelay(interval, unit);

    taskScheduler.start(
        () -> {
          logger.info("Executing task for robot: {}", robot);
          robot.run();
        },
        initialDelay,
        interval,
        unit);
    logger.info("Scheduler started.");
  }

  public void stop() {
    logger.info("Stopping scheduler for robot: {}", robot);
    taskScheduler.stop();
    logger.info("Scheduler stopped.");
  }
}
