package org.shadow.config;

import org.shadow.config.model.ExchangeConfiguration;
import org.shadow.infrastructure.fake.FakeExchangeOrderClient;

public class FakeExchangeOrderClientFactory
    implements ExchangeOrderClientFactory<FakeExchangeOrderClient> {

  @Override
  public FakeExchangeOrderClient createClient(ExchangeConfiguration exchangeConfiguration) {
    return new FakeExchangeOrderClient();
  }
}
