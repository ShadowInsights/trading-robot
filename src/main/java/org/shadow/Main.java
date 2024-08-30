package org.shadow;

import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.application.orchestration.RobotManager;
import org.shadow.application.orchestration.RobotScheduler;
import org.shadow.application.orchestration.TaskScheduler;
import org.shadow.application.robot.FakeRobot;

public class Main {

  private static final Logger logger = LogManager.getLogger(Main.class);

  public static void main(String[] args) {
    logger.info("Starting application...");

    var config = Config.load();

    var robot = new FakeRobot();
    var taskScheduler = new TaskScheduler(Executors.newScheduledThreadPool(1));
    var scheduler = new RobotScheduler(robot, 2, taskScheduler); // Logs every 2 seconds
    var robotManager =
        new RobotManager(Collections.singletonList(scheduler), Executors.newFixedThreadPool(1));

    // Start the robot
    robotManager.start();

    // Signal handling for graceful shutdown
    var running = new AtomicBoolean(true);
    Runtime.getRuntime()
        .addShutdownHook(
            new Thread(
                () -> {
                  logger.info("Shutdown signal received. Stopping robots...");
                  running.set(false);
                  try {
                    robotManager.stop();
                  } catch (InterruptedException e) {
                    logger.error("Error while stopping robots during shutdown", e);
                    Thread.currentThread().interrupt();
                  }
                  logger.info("Robots stopped. Application shutting down.");
                }));

    // Keep the application running until a shutdown signal is received
    while (running.get()) {
      try {
        TimeUnit.SECONDS.sleep(1); // Sleep for a second to avoid busy-waiting
      } catch (InterruptedException e) {
        logger.error("Main thread interrupted", e);
        Thread.currentThread().interrupt();
        break;
      }
    }

    logger.info("Application finished.");
  }
}
