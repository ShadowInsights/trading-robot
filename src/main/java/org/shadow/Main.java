package org.shadow;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.application.orchestration.RobotManager;
import org.shadow.application.orchestration.RobotScheduler;
import org.shadow.application.orchestration.TaskScheduler;
import org.shadow.application.robot.Robot;
import org.shadow.config.Config;
import org.shadow.config.FakeBarsCollectorClientFactory;
import org.shadow.config.FakeExchangeOrderClientFactory;
import org.shadow.config.SinglePositionRobotFactory;
import org.shadow.config.model.ExchangeConfiguration;
import org.shadow.config.model.RobotConfiguration;
import org.shadow.domain.client.BarsCollectorClient;
import org.shadow.domain.client.ExchangeOrderClient;

public class Main {

  private static final int TASK_SCHEDULER_THREAD_POOL_SIZE = 1;
  private static final Logger logger = LogManager.getLogger(Main.class);

  public static void main(String[] args) {
    logger.info("Starting application...");

    // Load configuration
    final var config = Config.load();

    // Create clients
    final var barsCollectorClient = createBarsCollectorClient(config.exchangeConfiguration());
    final var exchangeOrderClient = createExchangeOrderClient(config.exchangeConfiguration());

    // Create robots from configuration
    final var robots =
        createRobots(config.robotConfigurations(), barsCollectorClient, exchangeOrderClient);
    logger.info("Created {} robots", robots.size());

    // Create schedulers for each robot
    final var schedulers = createSchedulers(robots);

    // Initialize the RobotManager
    final var robotManager =
        new RobotManager(schedulers, Executors.newFixedThreadPool(schedulers.size()));

    // Start the RobotManager
    robotManager.start();

    final var running = new AtomicBoolean(true);

    // Handle shutdown
    handleShutdown(robotManager, running);

    // Main loop
    runMainLoop(running);

    logger.info("Application finished.");
  }

  private static BarsCollectorClient createBarsCollectorClient(
      ExchangeConfiguration exchangeConfiguration) {
    var fakeBarsCollectorClientFactory = new FakeBarsCollectorClientFactory();

    return switch (exchangeConfiguration.type()) {
      case FAKE -> fakeBarsCollectorClientFactory.createClient(exchangeConfiguration);
    };
  }

  private static ExchangeOrderClient createExchangeOrderClient(
      ExchangeConfiguration exchangeConfiguration) {
    var fakeExchangeOrderClientFactory = new FakeExchangeOrderClientFactory();

    return switch (exchangeConfiguration.type()) {
      case FAKE -> fakeExchangeOrderClientFactory.createClient(exchangeConfiguration);
    };
  }

  private static List<Robot> createRobots(
      List<RobotConfiguration> robotConfigurations,
      BarsCollectorClient barsCollectorClient,
      ExchangeOrderClient exchangeOrderClient) {
    var singlePositionRobotFactory = new SinglePositionRobotFactory();

    return robotConfigurations.stream()
        .<Robot>map(
            robotConfiguration ->
                switch (robotConfiguration.type()) {
                  case SINGLE_POSITION ->
                      singlePositionRobotFactory.createRobot(
                          robotConfiguration, barsCollectorClient, exchangeOrderClient);
                })
        .toList();
  }

  private static List<RobotScheduler> createSchedulers(List<Robot> robots) {
    return robots.stream()
        .map(
            robot -> {
              var taskScheduler =
                  new TaskScheduler(
                      Executors.newScheduledThreadPool(TASK_SCHEDULER_THREAD_POOL_SIZE));
              return new RobotScheduler(robot, taskScheduler);
            })
        .toList();
  }

  private static void handleShutdown(RobotManager robotManager, AtomicBoolean running) {
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
  }

  private static void runMainLoop(AtomicBoolean running) {
    while (running.get()) {
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        logger.error("Main thread interrupted", e);
        Thread.currentThread().interrupt();
        break;
      }
    }
  }
}
