package org.shadow.config.factory;

import org.shadow.config.exception.FailedToConstructException;
import org.shadow.config.model.ExchangeConfiguration;
import org.shadow.domain.client.BarsCollectorClient;
import org.shadow.infrastructure.client.fake.FakeBarsCollectorClient;
import org.shadow.infrastructure.client.fake.FakeExchangeOrderClient;
import org.shadow.infrastructure.client.fake.VirtualAccount;

public class FakeExchangeOrderClientFactory
    implements ExchangeOrderClientFactory<FakeExchangeOrderClient> {

  @Override
  public FakeExchangeOrderClient createClient(
      BarsCollectorClient fakeBarsCollectorClient, ExchangeConfiguration exchangeConfiguration) {
    if (exchangeConfiguration.virtualAccountInitialBalance().isPresent()) {
      var virtualAccount =
          new VirtualAccount(exchangeConfiguration.virtualAccountInitialBalance().get());
      return new FakeExchangeOrderClient(
          (FakeBarsCollectorClient) fakeBarsCollectorClient, virtualAccount);
    } else {
      throw new FailedToConstructException(FakeExchangeOrderClient.class);
    }
  }
}
