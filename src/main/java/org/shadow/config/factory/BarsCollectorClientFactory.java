package org.shadow.config.factory;

import org.shadow.config.model.ExchangeConfiguration;
import org.shadow.config.model.RobotConfiguration;
import org.shadow.domain.client.BarsCollectorClient;

public interface BarsCollectorClientFactory<T extends BarsCollectorClient> {

  T createClient(
      ExchangeConfiguration exchangeConfiguration, RobotConfiguration robotConfiguration);
}
