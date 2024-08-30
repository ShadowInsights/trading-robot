package org.shadow.application.orchestration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.application.robot.FakeRobot;

public class RobotScheduler {

  private static final Logger logger = LogManager.getLogger(RobotScheduler.class);

  private final FakeRobot robot;
  private final TaskScheduler taskScheduler;
  private final int logIntervalSeconds;

  public RobotScheduler(FakeRobot robot, int logIntervalSeconds, TaskScheduler taskScheduler) {
    this.robot = robot;
    this.taskScheduler = taskScheduler;
    this.logIntervalSeconds = logIntervalSeconds;
  }

  public void start() {
    logger.info("Starting scheduler for robot: {}", robot);
    logger.info("Log interval set to {} seconds.", logIntervalSeconds);
    taskScheduler.start(
        () -> {
          logger.info("Executing task for robot: {}", robot);
          robot.run();
        },
        logIntervalSeconds);
    logger.info("Scheduler started.");
  }

  public void stop() {
    logger.info("Stopping scheduler for robot: {}", robot);
    taskScheduler.stop();
    logger.info("Scheduler stopped.");
  }
}
