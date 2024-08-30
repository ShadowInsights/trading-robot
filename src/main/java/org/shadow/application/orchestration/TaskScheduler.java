package org.shadow.application.orchestration;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TaskScheduler {

  private static final Logger logger = LogManager.getLogger(TaskScheduler.class);

  private final ScheduledExecutorService scheduler;
  private ScheduledFuture<?> scheduledFuture;
  private final AtomicBoolean running = new AtomicBoolean(true);

  public TaskScheduler(ScheduledExecutorService scheduler) {
    this.scheduler = scheduler;
    logger.debug("TaskScheduler initialized with a single-threaded executor service.");
  }

  public void start(Runnable task, int intervalSeconds) {
    logger.info("Starting task scheduler with an interval of {} seconds.", intervalSeconds);
    scheduledFuture =
        scheduler.scheduleAtFixedRate(
            () -> {
              if (running.get()) {
                logger.debug("Executing scheduled task.");
                task.run();
              } else {
                logger.debug("Task execution is stopped; running flag is false.");
              }
            },
            0,
            intervalSeconds,
            TimeUnit.SECONDS);
    logger.info("Task scheduler started.");
  }

  public void stop() {
    logger.info("Stopping task scheduler.");
    running.set(false);
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
      logger.debug("Scheduled future cancelled.");
    }
    scheduler.shutdown();
    try {
      if (!scheduler.awaitTermination(10, TimeUnit.SECONDS)) {
        scheduler.shutdownNow();
        logger.warn("Scheduler did not terminate in the timeout period; forcefully shutting down.");
      }
    } catch (InterruptedException e) {
      scheduler.shutdownNow();
      Thread.currentThread().interrupt();
      logger.error("Interrupted while waiting for scheduler termination.", e);
    }
    logger.info("Task scheduler stopped.");
  }
}
