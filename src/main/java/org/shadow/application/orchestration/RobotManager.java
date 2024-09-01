package org.shadow.application.orchestration;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RobotManager {

  private static final Logger logger = LogManager.getLogger(RobotManager.class);

  private final List<RobotScheduler> robotSchedulers;
  private final ExecutorService executorService;

  public RobotManager(List<RobotScheduler> robotSchedulers, ExecutorService executorService) {
    this.robotSchedulers = robotSchedulers;
    this.executorService = executorService;
  }

  public void start() {
    robotSchedulers.forEach(
        scheduler -> {
          logger.debug("Starting robot scheduler: {}", scheduler);
          executorService.submit(scheduler::start);
        });
    logger.info("All robot schedulers have been started.");
  }

  public void stop() throws InterruptedException {
    logger.info("Stopping robot schedulers...");
    robotSchedulers.forEach(RobotScheduler::stop);

    executorService.shutdown();
    // TODO: executorServiceTerminationTimeout should be configured from contractor
    final var executorServiceTerminationTimeout = 10;
    var terminationResult =
        executorService.awaitTermination(executorServiceTerminationTimeout, TimeUnit.SECONDS);

    if (terminationResult) {
      logger.info("All robot schedulers have been stopped successfully.");
    } else {
      logger.warn("Timeout occurred while waiting for robot schedulers to stop.");
    }
  }
}
