package org.shadow.domain.client;

import java.math.BigDecimal;
import java.util.List;
import org.shadow.domain.client.model.Order;

public interface ExchangeOrderClient {

  void init();

  Order openLongOrder(
      String symbol,
      BigDecimal entry,
      List<BigDecimal> takeProfits,
      BigDecimal stopLoss,
      BigDecimal percentageFromDeposit,
      Integer futuresMultiplier);

  Order openShortOrder(
      String symbol,
      BigDecimal entry,
      List<BigDecimal> takeProfits,
      BigDecimal stopLoss,
      BigDecimal percentageFromDeposit,
      Integer futuresMultiplier);

  void closeOrder(Order id);
}
