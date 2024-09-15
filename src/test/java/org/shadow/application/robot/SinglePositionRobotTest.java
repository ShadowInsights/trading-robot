package org.shadow.application.robot;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.shadow.application.robot.common.model.Position;
import org.shadow.application.robot.common.model.PositionType;
import org.shadow.application.robot.strategy.Strategy;
import org.shadow.application.robot.strategy.model.BinaryPositionMomentum;
import org.shadow.application.robot.strategy.model.BinaryPositionMomentumActionType;
import org.shadow.domain.client.BarsCollectorClient;
import org.shadow.domain.client.ExchangeOrderClient;
import org.shadow.domain.client.model.Bar;
import org.shadow.domain.client.model.Order;

class SinglePositionRobotTest {

  @Mock private BarsCollectorClient barsCollectorClient;

  @Mock private ExchangeOrderClient exchangeOrderClient;

  @Mock private Strategy<BinaryPositionMomentum> binaryStrategy;

  private SinglePositionRobot robot;
  private final RobotTimeframe robotTimeframe = new RobotTimeframe(TimeUnit.MINUTES, 1);
  private final String symbol = "BTCUSD";
  private final BigDecimal percentageFromDeposit = BigDecimal.valueOf(0.01);
  private final Integer futuresMultiplier = 5;
  private final Instant initialBarsCollectionDate = Instant.now();
  private final Integer requiredBarsCount = 1;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    robot =
        new SinglePositionRobot(
            robotTimeframe,
            barsCollectorClient,
            exchangeOrderClient,
            binaryStrategy,
            symbol,
            percentageFromDeposit,
            futuresMultiplier,
            initialBarsCollectionDate,
            requiredBarsCount);

    when(barsCollectorClient.collectBars(any(), anyLong(), any(Instant.class), any(Instant.class)))
        .thenReturn(
            Collections.singletonList(
                new Bar(
                    Instant.now(),
                    BigDecimal.ONE,
                    BigDecimal.TEN,
                    BigDecimal.ZERO,
                    BigDecimal.ONE,
                    BigDecimal.TEN)));
  }

  @Test
  void testInit() {
    robot.init();
    assertEquals(RobotPositionState.EXPLORING, getPrivateRobotPositionState(robot));

    var bars = robot.getBars();
    assertNotNull(bars);
    assertEquals(1, bars.size());

    verify(barsCollectorClient)
        .collectBars(any(), anyLong(), any(Instant.class), any(Instant.class));
  }

  @Test
  void testRun_ExploringState() {
    robot.init();
    when(binaryStrategy.calculatePositionMomentum(any()))
        .thenReturn(
            new BinaryPositionMomentum(
                BinaryPositionMomentumActionType.DO_NOTHING, Optional.empty()));

    robot.run();

    verify(barsCollectorClient, times(2))
        .collectBars(any(), anyLong(), any(Instant.class), any(Instant.class));
  }

  @Test
  void testRun_InPositionState() {
    var order = mock(Order.class);
    var position =
        new Position(
            PositionType.LONG,
            BigDecimal.valueOf(150.0),
            Collections.emptyList(),
            Optional.of(BigDecimal.valueOf(100.0)));
    robot.setOrder(order);
    robot.setPosition(position);
    robot.setRobotPositionState(RobotPositionState.IN_POSITION);

    when(binaryStrategy.isTimeToClosePositionInAdvance(any(), eq(position))).thenReturn(false);

    robot.run();

    verify(barsCollectorClient, times(1))
        .collectBars(any(), anyLong(), any(Instant.class), any(Instant.class));
    verify(exchangeOrderClient, never()).closeOrder(any());
  }

  @Test
  void testStop() {
    robot.init();
    // TODO: Add assertion when inside method is implemented additional logic
    robot.stop();
  }

  @Test
  void testGetters() {
    assertEquals(robotTimeframe, robot.getRobotTimeframe());
    assertEquals(symbol, robot.getSymbol());
    assertEquals(percentageFromDeposit, robot.getPercentageFromDeposit());
    assertEquals(futuresMultiplier, robot.getOrderFuturesMultiplier());
  }

  @Test
  void testSetPosition() {
    var position =
        new Position(
            PositionType.LONG,
            BigDecimal.valueOf(150.0),
            Collections.emptyList(),
            Optional.of(BigDecimal.valueOf(100.0)));
    robot.setPosition(position);
    assertEquals(List.of(position), robot.getPositions());
  }

  @Test
  void testSetOrder() {
    var order = mock(Order.class);
    robot.setOrder(order);
    assertEquals(order, robot.getOrder());
  }

  @Test
  void testSetRobotPositionState() {
    robot.setRobotPositionState(RobotPositionState.IN_POSITION);
    assertEquals(RobotPositionState.IN_POSITION, getPrivateRobotPositionState(robot));
  }

  private RobotPositionState getPrivateRobotPositionState(SinglePositionRobot robot) {
    try {
      Field field = SinglePositionRobot.class.getDeclaredField("robotPositionState");
      field.setAccessible(true);
      return (RobotPositionState) field.get(robot);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }
}
