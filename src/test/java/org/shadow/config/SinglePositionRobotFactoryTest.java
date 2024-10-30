package org.shadow.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.shadow.application.robot.explorer.RSIBinaryExplorer;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;
import org.shadow.application.robot.strategy.BinaryStrategy;
import org.shadow.config.factory.SinglePositionRobotFactory;
import org.shadow.config.model.OrderConfiguration;
import org.shadow.config.model.RobotConfiguration;
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
    var orderConfig = mock(OrderConfiguration.class);
    var robotConfig = mock(RobotConfiguration.class);

    // Configure mock behavior
    when(robotConfig.unit()).thenReturn(TimeUnit.MINUTES);
    when(robotConfig.interval()).thenReturn(1L);
    when(robotConfig.symbol()).thenReturn("BTCUSD");
    when(orderConfig.allowedOrderPercentageFromDeposit()).thenReturn(BigDecimal.valueOf(0.01));
    when(orderConfig.allowedOrderFuturesMultiplier()).thenReturn(5);
    when(orderConfig.stopLossRequiredPercentage()).thenReturn(BigDecimal.valueOf(0.02));
    when(robotConfig.orderConfiguration()).thenReturn(orderConfig);

    var robot = robotFactory.createRobot(robotConfig, barsCollectorClient, exchangeOrderClient);

    assertNotNull(robot, "The robot should not be null");
    assertEquals("BTCUSD", robot.getSymbol(), "The robot symbol should match the configuration");

    var strategy = (BinaryStrategy) robot.getStrategy();
    assertNotNull(strategy, "The binary strategy should not be null");

    var explorers = strategy.getBinaryExplorers();
    assertEquals(2, explorers.size(), "There should be one explorer");
    assertEquals(
        RSIBinaryExplorer.class,
        explorers.getFirst().getClass(),
        "The explorer should be an instance of RSIBinaryExplorer");

    var multipliers = strategy.getBinaryIsMomentumExplorationStateIntegerMultiplierMap();
    assertEquals(
        SinglePositionRobotFactory.NOT_READY_MULTIPLIER,
        multipliers.get(BinaryIsMomentumExplorationState.NOT_READY));
    assertEquals(
        SinglePositionRobotFactory.MINOR_MULTIPLIER,
        multipliers.get(BinaryIsMomentumExplorationState.MINOR));
    assertEquals(
        SinglePositionRobotFactory.MEDIUM_MULTIPLIER,
        multipliers.get(BinaryIsMomentumExplorationState.MEDIUM));
    assertEquals(
        SinglePositionRobotFactory.MAJOR_MULTIPLIER,
        multipliers.get(BinaryIsMomentumExplorationState.MAJOR));
  }
}
