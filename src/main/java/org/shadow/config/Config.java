package org.shadow.config;

import com.typesafe.config.ConfigFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.shadow.config.model.ExchangeConfiguration;
import org.shadow.config.model.ExchangeConfigurationType;
import org.shadow.config.model.OrderConfiguration;
import org.shadow.config.model.RobotConfiguration;
import org.shadow.config.model.RobotType;

public record Config(
    List<RobotConfiguration> robotConfigurations, ExchangeConfiguration exchangeConfiguration) {

  public static Config load() {
    var config = ConfigFactory.load();

    var exchangeConfig =
        new ExchangeConfiguration(
            ExchangeConfigurationType.valueOf(config.getString("exchange.type")),
            config.getString("exchange.apiKey"),
            config.getString("exchange.apiSecret"),
            Optional.of(
                BigDecimal.valueOf(config.getDouble("exchange.virtualAccountInitialBalance"))));

    var robotConfigs =
        config.getConfigList("robots").stream()
            .map(
                robotConfig ->
                    new RobotConfiguration(
                        RobotType.valueOf(robotConfig.getString("type")),
                        TimeUnit.valueOf(robotConfig.getString("unit")),
                        robotConfig.getLong("interval"),
                        robotConfig.getString("symbol"),
                        new OrderConfiguration(
                            BigDecimal.valueOf(
                                robotConfig.getDouble("allowedOrderPercentageFromDeposit")),
                            robotConfig.getInt("allowedOrderFuturesMultiplier"),
                            BigDecimal.valueOf(
                                robotConfig.getDouble("stopLossRequiredPercentage"))),
                        Optional.of(robotConfig.getString("historicalDataFile"))))
            .toList();

    return new Config(robotConfigs, exchangeConfig);
  }
}
