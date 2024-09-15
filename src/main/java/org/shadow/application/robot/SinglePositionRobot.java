package org.shadow.application.robot;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Queue;
import org.apache.commons.collections4.queue.CircularFifoQueue;
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

  private final Logger logger = LogManager.getLogger(SinglePositionRobot.class);

  private final RobotTimeframe robotTimeframe;
  private final BarsCollectorClient barsCollectorClient;
  private final Strategy<BinaryPositionMomentum> binaryStrategy;
  private final ExchangeOrderClient exchangeOrderClient;
  private final String symbol;
  private RobotPositionState robotPositionState;
  private final BigDecimal percentageFromDeposit;
  private final Integer futuresMultiplier;
  private final Instant initialBarsCollectionDate;
  private final Integer requiredBarsCount;
  private final Queue<Bar> bars;

  private Position position;
  private Order order;

  public SinglePositionRobot(
      RobotTimeframe robotTimeframe,
      BarsCollectorClient barsCollectorClient,
      ExchangeOrderClient exchangeOrderClient,
      Strategy<BinaryPositionMomentum> binaryStrategy,
      String symbol,
      BigDecimal percentageFromDeposit,
      Integer futuresMultiplier,
      Instant initialBarsCollectionDate,
      Integer requiredBarsCount) {
    this.robotTimeframe = robotTimeframe;
    this.barsCollectorClient = barsCollectorClient;
    this.binaryStrategy = binaryStrategy;
    this.exchangeOrderClient = exchangeOrderClient;
    this.symbol = symbol;
    this.percentageFromDeposit = percentageFromDeposit;
    this.futuresMultiplier = futuresMultiplier;
    this.initialBarsCollectionDate = initialBarsCollectionDate;
    this.requiredBarsCount = requiredBarsCount;
    this.bars = new CircularFifoQueue<>(requiredBarsCount);
  }

  @Override
  public synchronized void init() {
    logger.info("Initializing robot...");
    // TODO: Load open position from exchangeOrderClient when position loading implemented
    position = null;
    robotPositionState = RobotPositionState.EXPLORING;

    collectBars();
    if (bars.size() < requiredBarsCount) {
      logger.warn(
          "Not enough bars collected during initialization. Required: {}, actual: {}",
          requiredBarsCount,
          bars.size());
    }

    logger.info("Robot initialized. Current state: {}", robotPositionState);
  }

  @Override
  public synchronized void run() {
    try {
      collectBars();
      logger.debug("Collected {} bars", bars.size());

      if (bars.size() < requiredBarsCount) {
        logger.warn(
            "Not enough bars to run strategy. Required: {}, actual: {}",
            requiredBarsCount,
            bars.size());
        return;
      }

      var handler = getPositionHandler();
      handler.handle(this);

      logger.info("Position state after run: {}", robotPositionState);
    } catch (Exception e) {
      logger.error("Failed to execute robot cycle", e);
    }
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
    return bars.stream().toList();
  }

  public Strategy<BinaryPositionMomentum> getStrategy() {
    return binaryStrategy;
  }

  public ExchangeOrderClient getExchangeOrderClient() {
    return exchangeOrderClient;
  }

  public void setPosition(Position position) {
    this.position = position;
    logger.info("Position set: {}", position);
  }

  public void setOrder(Order order) {
    this.order = order;
    logger.info("Order set: {}", order);
  }

  public void setRobotPositionState(RobotPositionState state) {
    this.robotPositionState = state;
  }

  public Order getOrder() {
    return order;
  }

  private void collectBars() {
    var timeFrom = bars.peek() != null ? bars.peek().time() : initialBarsCollectionDate;
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
    // TODO: Implement filtering
    //    var filteredCollectedBars = collectedBars.stream().filter(bar ->
    // bar.time().isAfter(timeFrom)).toList();
    //    bars.addAll(filteredCollectedBars);

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
