package org.shadow.application.robot;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Optional;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.shadow.application.robot.common.model.Position;
import org.shadow.application.robot.common.model.PositionType;
import org.shadow.application.robot.strategy.BinaryStrategy;
import org.shadow.domain.client.ExchangeOrderClient;
import org.shadow.domain.client.model.Order;

class InSinglePositionHandlerTest {

  @Mock private SinglePositionRobot robot;

  @Mock private BinaryStrategy strategy;

  @Mock private ExchangeOrderClient exchangeOrderClient;

  @Mock private Order order;

  private InSinglePositionHandler positionHandler;

  private Position currentPosition;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    positionHandler = new InSinglePositionHandler();

    when(robot.getStrategy()).thenReturn(strategy);
    when(robot.getExchangeOrderClient()).thenReturn(exchangeOrderClient);
    when(robot.getOrder()).thenReturn(order);
    when(robot.getLogger()).thenReturn(mock(Logger.class));

    currentPosition =
        new Position(
            PositionType.LONG,
            BigDecimal.valueOf(150.0),
            Collections.emptyList(),
            Optional.of(BigDecimal.valueOf(100.0)));

    var positionList = new LinkedList<Position>();
    positionList.add(currentPosition);
    when(robot.getPositions()).thenReturn(positionList);
  }

  @Test
  void testHandle_WhenTimeToClosePosition() {
    when(strategy.isTimeToClosePositionInAdvance(any(), eq(currentPosition))).thenReturn(true);

    positionHandler.handle(robot);

    verify(exchangeOrderClient).closeOrder(order);
    verify(robot).setPosition(null);
    verify(robot).setOrder(null);
    verify(robot).setRobotPositionState(RobotPositionState.EXPLORING);
  }

  @Test
  void testHandle_WhenNotTimeToClosePosition() {
    when(strategy.isTimeToClosePositionInAdvance(any(), eq(currentPosition))).thenReturn(false);

    positionHandler.handle(robot);

    verify(exchangeOrderClient, never()).closeOrder(any());
    verify(robot, never()).setPosition(null);
    verify(robot, never()).setOrder(null);
    verify(robot, never()).setRobotPositionState(any());
  }
}
