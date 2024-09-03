package org.shadow.infrastructure.fake;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.domain.client.ExchangeOrderClient;
import org.shadow.domain.client.model.Order;
import org.shadow.domain.client.model.OrderType;

public class FakeExchangeOrderClient implements ExchangeOrderClient {

  private final Logger logger = LogManager.getLogger(FakeExchangeOrderClient.class);
  private static long orderIdCounter = 1;

  @Override
  public void init() {
    logger.info("Initializing FakeExchangeOrderClient...");
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

    var order = new Order(generateOrderId(), entry, Instant.now(), OrderType.MARKET);

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

    var order = new Order(generateOrderId(), entry, Instant.now(), OrderType.MARKET);

    logger.debug("Short order created: {}", order);

    return order;
  }

  @Override
  public void closeOrder(Order order) {
    logger.info("Closing order - ID: {}, Type: {}", order.id(), order.type());
    // In a real implementation, logic to close the order would be here
    logger.debug("Order closed: {}", order);
  }

  private static long generateOrderId() {
    return orderIdCounter++;
  }
}
