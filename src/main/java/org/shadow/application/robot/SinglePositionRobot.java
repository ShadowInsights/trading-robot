package org.shadow.application.robot;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.common.model.Position;
import org.shadow.application.robot.strategy.Strategy;
import org.shadow.application.robot.strategy.model.BinaryPositionMomentum;
import org.shadow.domain.client.BarsCollectorClient;
import org.shadow.domain.client.ExchangeOrderClient;
import org.shadow.domain.client.model.Order;

public class SinglePositionRobot implements Robot {

  private final List<Bar> bars = new LinkedList<>();
  private final Logger logger = LogManager.getLogger(SinglePositionRobot.class);

  private final RobotTimeframe robotTimeframe;
  private final BarsCollectorClient barsCollectorClient;
  private final org.shadow.application.robot.strategy.Strategy<BinaryPositionMomentum>
      binaryStrategy;
  private final ExchangeOrderClient exchangeOrderClient;
  private final String symbol;

  private Position position;
  private Order order;
  private RobotPositionState robotPositionState;
  private final BigDecimal percentageFromDeposit;
  private final Integer futuresMultiplier;

  public SinglePositionRobot(
      RobotTimeframe robotTimeframe,
      BarsCollectorClient barsCollectorClient,
      ExchangeOrderClient exchangeOrderClient,
      Strategy<BinaryPositionMomentum> binaryStrategy,
      String symbol,
      BigDecimal percentageFromDeposit,
      Integer futuresMultiplier) {
    this.robotTimeframe = robotTimeframe;
    this.barsCollectorClient = barsCollectorClient;
    this.binaryStrategy = binaryStrategy;
    this.exchangeOrderClient = exchangeOrderClient;
    this.symbol = symbol;
    this.percentageFromDeposit = percentageFromDeposit;
    this.futuresMultiplier = futuresMultiplier;
  }

  @Override
  public void init() {
    // TODO: Load open position from exchangeOrderClient when position loading implemented
    position = null;
    robotPositionState = RobotPositionState.EXPLORING;

    collectBars();
    logger.info("Robot initialized. Current state: {}", robotPositionState);
  }

  @Override
  public void run() {
    collectBars();
    logger.debug("Collected bars: {}", bars);

    var handler = getPositionHandler();
    handler.handle(this);

    logger.info("Position state after run: {}", robotPositionState);
  }

  @Override
  public void stop() {
    // TODO: Implement logic
    logger.info("Stopping robot. Current position state: {}", robotPositionState);
  }

  @Override
  public RobotTimeframe getRobotTimeframe() {
    return robotTimeframe;
  }

  @Override
  public String getSymbol() {
    return symbol;
  }

  @Override
  public List<Position> getPositions() {
    return position != null ? List.of(position) : List.of();
  }

  @Override
  public BigDecimal getPercentageFromDeposit() {
    return percentageFromDeposit;
  }

  @Override
  public Integer getOrderFuturesMultiplier() {
    return futuresMultiplier;
  }

  public Logger getLogger() {
    return logger;
  }

  public List<Bar> getBars() {
    return bars;
  }

  public Strategy<BinaryPositionMomentum> getStrategy() {
    return binaryStrategy;
  }

  public ExchangeOrderClient getExchangeOrderClient() {
    return exchangeOrderClient;
  }

  public void setPosition(Position position) {
    this.position = position;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public void setRobotPositionState(RobotPositionState state) {
    this.robotPositionState = state;
  }

  public Order getOrder() {
    return order;
  }

  private void collectBars() {
    // TODO: Replace Instant.MIN with initial bars collection date, has to be configured from
    //  constructor
    var timeFrom = bars.isEmpty() ? Instant.MIN : bars.getLast().time();
    var timeTo = Instant.now();
    var collectedBars =
        barsCollectorClient
            .collectBars(robotTimeframe.unit(), robotTimeframe.interval(), timeFrom, timeTo)
            .stream()
            .map(
                bar ->
                    new Bar(
                        bar.time(), bar.open(), bar.high(), bar.low(), bar.close(), bar.volume()))
            .toList();
    bars.addAll(collectedBars);
    logger.info("Bars collected from {} to {}: {}", timeFrom, timeTo, bars.size());
  }

  private RobotPositionHandler<SinglePositionRobot> getPositionHandler() {
    return switch (robotPositionState) {
      case EXPLORING -> new ExploringSinglePositionHandler();
      case IN_POSITION -> new InSinglePositionHandler();
    };
  }
}
