package org.shadow.config;

import org.shadow.config.model.ExchangeConfiguration;
import org.shadow.domain.client.BarsCollectorClient;

public interface BarsCollectorClientFactory<T extends BarsCollectorClient> {

  T createClient(ExchangeConfiguration exchangeConfiguration);
}
