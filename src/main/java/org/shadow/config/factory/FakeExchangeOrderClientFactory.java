package org.shadow.config.factory;

import org.shadow.config.model.ExchangeConfiguration;
import org.shadow.infrastructure.client.fake.FakeExchangeOrderClient;

public class FakeExchangeOrderClientFactory
    implements ExchangeOrderClientFactory<FakeExchangeOrderClient> {

  @Override
  public FakeExchangeOrderClient createClient(ExchangeConfiguration exchangeConfiguration) {
    return new FakeExchangeOrderClient();
  }
}
