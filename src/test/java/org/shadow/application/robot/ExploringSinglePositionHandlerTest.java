package org.shadow.application.robot;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.shadow.application.robot.common.model.Position;
import org.shadow.application.robot.common.model.PositionType;
import org.shadow.application.robot.strategy.BinaryStrategy;
import org.shadow.application.robot.strategy.model.BinaryPositionMomentum;
import org.shadow.application.robot.strategy.model.BinaryPositionMomentumActionType;
import org.shadow.domain.client.ExchangeOrderClient;
import org.shadow.domain.client.model.Order;
import org.shadow.domain.client.model.OrderType;

class ExploringSinglePositionHandlerTest {

  @Mock private SinglePositionRobot robot;

  @Mock private BinaryStrategy strategy;

  @Mock private ExchangeOrderClient exchangeOrderClient;

  private ExploringSinglePositionHandler positionHandler;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    positionHandler = new ExploringSinglePositionHandler();

    when(robot.getStrategy()).thenReturn(strategy);
    when(robot.getExchangeOrderClient()).thenReturn(exchangeOrderClient);
    when(robot.getLogger()).thenReturn(mock(Logger.class));
  }

  @Test
  void testHandle_LongMomentum_WithStopLoss() {
    var stopLoss = Optional.of(BigDecimal.valueOf(100.0));
    var momentum = new BinaryPositionMomentum(BinaryPositionMomentumActionType.LONG, stopLoss);
    when(strategy.calculatePositionMomentum(any())).thenReturn(momentum);

    var order = new Order(1L, BigDecimal.valueOf(150.0), Instant.now(), OrderType.MARKET);
    when(exchangeOrderClient.openLongOrder(any(), any(), any(), any(), any(), any()))
        .thenReturn(order);

    when(robot.getSymbol()).thenReturn("BTCUSD");
    when(robot.getPercentageFromDeposit()).thenReturn(BigDecimal.valueOf(0.01));
    when(robot.getOrderFuturesMultiplier()).thenReturn(5);

    positionHandler.handle(robot);

    verify(exchangeOrderClient)
        .openLongOrder(
            eq("BTCUSD"),
            eq(null),
            eq(null),
            eq(BigDecimal.valueOf(100.0)),
            eq(BigDecimal.valueOf(0.01)),
            eq(5));

    verify(robot).setPosition(new Position(PositionType.LONG, order.entry(), List.of(), stopLoss));
    verify(robot).setOrder(order);
    verify(robot).setRobotPositionState(RobotPositionState.IN_POSITION);
  }

  @Test
  void testHandle_ShortMomentum_WithStopLoss() {
    var stopLoss = Optional.of(BigDecimal.valueOf(100.0));
    var momentum = new BinaryPositionMomentum(BinaryPositionMomentumActionType.SHORT, stopLoss);
    when(strategy.calculatePositionMomentum(any())).thenReturn(momentum);

    var order = new Order(2L, BigDecimal.valueOf(150.0), Instant.now(), OrderType.MARKET);
    when(exchangeOrderClient.openShortOrder(any(), any(), any(), any(), any(), any()))
        .thenReturn(order);

    when(robot.getSymbol()).thenReturn("BTCUSD");
    when(robot.getPercentageFromDeposit()).thenReturn(BigDecimal.valueOf(0.01));
    when(robot.getOrderFuturesMultiplier()).thenReturn(5);

    positionHandler.handle(robot);

    verify(exchangeOrderClient)
        .openShortOrder(
            eq("BTCUSD"),
            eq(null),
            eq(null),
            eq(BigDecimal.valueOf(100.0)),
            eq(BigDecimal.valueOf(0.01)),
            eq(5));

    verify(robot).setPosition(new Position(PositionType.SHORT, order.entry(), List.of(), stopLoss));
    verify(robot).setOrder(order);
    verify(robot).setRobotPositionState(RobotPositionState.IN_POSITION);
  }

  @Test
  void testHandle_LongMomentum_WithoutStopLoss() {
    var momentum =
        new BinaryPositionMomentum(BinaryPositionMomentumActionType.LONG, Optional.empty());
    when(strategy.calculatePositionMomentum(any())).thenReturn(momentum);

    positionHandler.handle(robot);

    verifyNoInteractions(exchangeOrderClient);
  }

  @Test
  void testHandle_ShortMomentum_WithoutStopLoss() {
    var momentum =
        new BinaryPositionMomentum(BinaryPositionMomentumActionType.SHORT, Optional.empty());
    when(strategy.calculatePositionMomentum(any())).thenReturn(momentum);

    positionHandler.handle(robot);

    verifyNoInteractions(exchangeOrderClient);
  }

  @Test
  void testHandle_DoNothingMomentum() {
    var momentum =
        new BinaryPositionMomentum(BinaryPositionMomentumActionType.DO_NOTHING, Optional.empty());
    when(strategy.calculatePositionMomentum(any())).thenReturn(momentum);

    positionHandler.handle(robot);

    verifyNoInteractions(exchangeOrderClient);
  }
}
