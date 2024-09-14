package org.shadow.config.factory;

import org.shadow.config.exception.FailedToConstructException;
import org.shadow.config.model.ExchangeConfiguration;
import org.shadow.config.model.RobotConfiguration;
import org.shadow.infrastructure.client.fake.FakeBarsCollectorClient;
import org.shadow.infrastructure.file.HistoricalDataLoader;

public class FakeBarsCollectorClientFactory
    implements BarsCollectorClientFactory<FakeBarsCollectorClient> {

  @Override
  public FakeBarsCollectorClient createClient(
      ExchangeConfiguration exchangeConfiguration, RobotConfiguration robotConfiguration) {
    if (robotConfiguration.historicalDataFile().isPresent()) {
      return new FakeBarsCollectorClient(
          new HistoricalDataLoader(), robotConfiguration.historicalDataFile().get());
    } else {
      throw new FailedToConstructException(FakeBarsCollectorClient.class);
    }
  }
}
