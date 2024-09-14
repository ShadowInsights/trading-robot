package org.shadow.config.factory;

import org.shadow.config.model.ExchangeConfiguration;
import org.shadow.domain.client.ExchangeOrderClient;

public interface ExchangeOrderClientFactory<T extends ExchangeOrderClient> {

  T createClient(ExchangeConfiguration exchangeConfiguration);
}
