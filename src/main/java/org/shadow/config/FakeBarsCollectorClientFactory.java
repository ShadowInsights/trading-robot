package org.shadow.config;

import org.shadow.config.model.ExchangeConfiguration;
import org.shadow.infrastructure.fake.FakeBarsCollectorClient;

public class FakeBarsCollectorClientFactory
    implements BarsCollectorClientFactory<FakeBarsCollectorClient> {

  @Override
  public FakeBarsCollectorClient createClient(ExchangeConfiguration exchangeConfiguration) {
    return new FakeBarsCollectorClient();
  }
}
