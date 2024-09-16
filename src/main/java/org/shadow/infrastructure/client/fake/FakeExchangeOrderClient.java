package org.shadow.infrastructure.client.fake;

import java.math.BigDecimal;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.application.robot.common.model.PositionType;
import org.shadow.domain.client.ExchangeOrderClient;
import org.shadow.domain.client.model.Order;

/** A fake implementation of ExchangeOrderClient for testing purposes. */
public class FakeExchangeOrderClient implements ExchangeOrderClient {

  private static final Logger logger = LogManager.getLogger(FakeExchangeOrderClient.class);
  private final VirtualAccount virtualAccount;
  private final FakeBarsCollectorClient fakeBarsCollectorClient;

  /** Initializes the FakeExchangeOrderClient with a new VirtualAccount. */
  public FakeExchangeOrderClient(
      FakeBarsCollectorClient fakeBarsCollectorClient, VirtualAccount virtualAccount) {
    this.fakeBarsCollectorClient = fakeBarsCollectorClient;
    this.virtualAccount = virtualAccount;
    logger.info("FakeExchangeOrderClient instantiated.");
  }

  @Override
  public void init() {
    logger.info("FakeExchangeOrderClient initialized.");
  }

  @Override
  public Order openLongOrder(
      String symbol,
      BigDecimal entry,
      List<BigDecimal> takeProfits,
      BigDecimal stopLoss,
      BigDecimal percentageFromDeposit,
      Integer futuresMultiplier) {
    logger.info(
        "Opening long order - Symbol: {}, Entry: {}, TakeProfits: {}, StopLoss: {}, PercentageFromDeposit: {}, FuturesMultiplier: {}",
        symbol,
        entry,
        takeProfits,
        stopLoss,
        percentageFromDeposit,
        futuresMultiplier);

    var order =
        virtualAccount.openOrder(
            PositionType.LONG, calculateEntryPrice(entry), percentageFromDeposit);
    logger.debug("Long order created: {}", order);

    return order;
  }

  @Override
  public Order openShortOrder(
      String symbol,
      BigDecimal entry,
      List<BigDecimal> takeProfits,
      BigDecimal stopLoss,
      BigDecimal percentageFromDeposit,
      Integer futuresMultiplier) {
    logger.info(
        "Opening short order - Symbol: {}, Entry: {}, TakeProfits: {}, StopLoss: {}, PercentageFromDeposit: {}, FuturesMultiplier: {}",
        symbol,
        entry,
        takeProfits,
        stopLoss,
        percentageFromDeposit,
        futuresMultiplier);

    var order =
        virtualAccount.openOrder(
            PositionType.SHORT, calculateEntryPrice(entry), percentageFromDeposit);
    logger.debug("Short order created: {}", order);

    return order;
  }

  @Override
  public void closeOrder(Order order) {
    logger.info("Closing order - ID: {}, Type: {}", order.id(), order.type());
    var exitPrice = fakeBarsCollectorClient.getCurrentPrice();
    virtualAccount.closeOrder(order, exitPrice);
  }

  /**
   * Retrieves the current virtual balance.
   *
   * @return Current balance as BigDecimal.
   */
  public BigDecimal getVirtualBalance() {
    var balance = virtualAccount.getBalance();
    logger.info("Current virtual balance: {}", balance);
    return balance;
  }

  /**
   * Retrieves a list of all open orders.
   *
   * @return List of open orders.
   */
  public List<Order> getOpenOrders() {
    var openOrders = virtualAccount.getOpenOrders();
    logger.info("Retrieved {} open orders.", openOrders.size());
    return openOrders;
  }

  /**
   * Retrieves a list of all closed orders.
   *
   * @return List of closed orders.
   */
  public List<Order> getClosedOrders() {
    var closedOrders = virtualAccount.getClosedOrders();
    logger.info("Retrieved {} closed orders.", closedOrders.size());
    return closedOrders;
  }

  private BigDecimal calculateEntryPrice(BigDecimal entry) {
    return entry == null ? fakeBarsCollectorClient.getCurrentPrice() : entry;
  }
}
