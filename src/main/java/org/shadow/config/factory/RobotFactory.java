package org.shadow.config.factory;

import org.shadow.application.robot.Robot;
import org.shadow.config.model.RobotConfiguration;
import org.shadow.domain.client.BarsCollectorClient;
import org.shadow.domain.client.ExchangeOrderClient;

public interface RobotFactory<T extends Robot> {

  T createRobot(
      RobotConfiguration robotConfiguration,
      BarsCollectorClient barsCollectorClient,
      ExchangeOrderClient exchangeOrderClient);
}
