package org.shadow.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.shadow.application.robot.explorer.BollingerBandsBinaryExplorer;
import org.shadow.application.robot.explorer.MACDBinaryExplorer;
import org.shadow.application.robot.explorer.RSIBinaryExplorer;
import org.shadow.application.robot.explorer.StochasticOscillatorExplorer;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;
import org.shadow.application.robot.strategy.BinaryStrategy;
import org.shadow.config.factory.SinglePositionRobotFactory;
import org.shadow.config.model.BollingerBandsExplorerConfiguration;
import org.shadow.config.model.MACDExplorerConfiguration;
import org.shadow.config.model.OrderConfiguration;
import org.shadow.config.model.RSIExplorerConfiguration;
import org.shadow.config.model.RobotConfiguration;
import org.shadow.config.model.RobotType;
import org.shadow.config.model.StochasticOscillatorExplorerConfiguration;
import org.shadow.domain.client.BarsCollectorClient;
import org.shadow.domain.client.ExchangeOrderClient;

class SinglePositionRobotFactoryTest {

  private SinglePositionRobotFactory robotFactory;

  @BeforeEach
  void setUp() {
    robotFactory = new SinglePositionRobotFactory();
  }

  @Test
  void testCreateRobot() {
    // Mock dependencies
    var barsCollectorClient = mock(BarsCollectorClient.class);
    var exchangeOrderClient = mock(ExchangeOrderClient.class);

    // Create OrderConfiguration
    var orderConfig =
        new OrderConfiguration(
            0.01, // allowedOrderPercentageFromDeposit
            5, // allowedOrderFuturesMultiplier
            0.02 // stopLossRequiredPercentage
            );

    // Create Explorer Configurations
    var rsiConfig =
        new RSIExplorerConfiguration(
            1, // severity
            14, // period
            30.0, // oversoldThreshold
            70.0, // overboughtThreshold
            40.0, // longMediumThreshold
            60.0, // shortMediumThreshold
            50.0, // longMinorThreshold
            50.0 // shortMinorThreshold
            );

    var macdConfig =
        new MACDExplorerConfiguration(
            1, // severity
            12, // shortPeriod
            26, // longPeriod
            9, // signalPeriod
            0.5, // histogramMajorThreshold
            0.2, // histogramMediumThreshold
            0.05 // histogramMinorThreshold
            );

    var bollingerConfig =
        new BollingerBandsExplorerConfiguration(
            1, // severity
            20, // period
            2.0, // standardDeviationMultiplier
            0.0, // lowerBandThreshold
            1.0, // upperBandThreshold
            0.2, // longMediumThreshold
            0.8, // shortMediumThreshold
            0.4, // longMinorThreshold
            0.6 // shortMinorThreshold
            );

    var stochasticConfig =
        new StochasticOscillatorExplorerConfiguration(
            1, // severity
            14, // period
            3, // dPeriod
            20.0, // oversoldThreshold
            80.0, // overboughtThreshold
            30.0, // longMediumThreshold
            70.0, // shortMediumThreshold
            40.0, // longMinorThreshold
            60.0 // shortMinorThreshold
            );

    // Create RobotConfiguration
    var robotConfig =
        new RobotConfiguration(
            RobotType.SINGLE_POSITION,
            TimeUnit.MINUTES,
            1L,
            "BTCUSD",
            orderConfig,
            Optional.empty(),
            rsiConfig,
            macdConfig,
            bollingerConfig,
            stochasticConfig,
            0, // notReadyMultiplier
            1, // minorMultiplier
            2, // mediumMultiplier
            3 // majorMultiplier
            );

    // Create the robot using the factory
    var robot = robotFactory.createRobot(robotConfig, barsCollectorClient, exchangeOrderClient);

    // Assertions
    assertNotNull(robot, "The robot should not be null");
    assertEquals("BTCUSD", robot.getSymbol(), "The robot symbol should match the configuration");
    assertEquals(TimeUnit.MINUTES, robot.getRobotTimeframe().unit(), "Time unit should match");
    assertEquals(1L, robot.getRobotTimeframe().interval(), "Interval should match");

    // Check strategy
    var strategy = (BinaryStrategy) robot.getStrategy();
    assertNotNull(strategy, "The binary strategy should not be null");

    // Check explorers
    var explorers = strategy.getBinaryExplorers();
    assertEquals(4, explorers.size(), "There should be four explorers");

    // Assert types of explorers
    assertEquals(
        RSIBinaryExplorer.class,
        explorers.get(0).getClass(),
        "First explorer should be RSIBinaryExplorer");
    assertEquals(
        MACDBinaryExplorer.class,
        explorers.get(1).getClass(),
        "Second explorer should be MACDBinaryExplorer");
    assertEquals(
        BollingerBandsBinaryExplorer.class,
        explorers.get(2).getClass(),
        "Third explorer should be BollingerBandsBinaryExplorer");
    assertEquals(
        StochasticOscillatorExplorer.class,
        explorers.get(3).getClass(),
        "Fourth explorer should be StochasticOscillatorExplorer");

    // Check multipliers
    var multipliers = strategy.getBinaryIsMomentumExplorationStateIntegerMultiplierMap();
    assertEquals(0, multipliers.get(BinaryIsMomentumExplorationState.NOT_READY));
    assertEquals(1, multipliers.get(BinaryIsMomentumExplorationState.MINOR));
    assertEquals(2, multipliers.get(BinaryIsMomentumExplorationState.MEDIUM));
    assertEquals(3, multipliers.get(BinaryIsMomentumExplorationState.MAJOR));
  }
}
