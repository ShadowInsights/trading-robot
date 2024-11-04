package org.shadow.config;

import com.typesafe.config.ConfigFactory;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.shadow.config.model.BollingerBandsExplorerConfiguration;
import org.shadow.config.model.ExchangeConfiguration;
import org.shadow.config.model.ExchangeConfigurationType;
import org.shadow.config.model.MACDExplorerConfiguration;
import org.shadow.config.model.OrderConfiguration;
import org.shadow.config.model.RSIExplorerConfiguration;
import org.shadow.config.model.RobotConfiguration;
import org.shadow.config.model.RobotType;
import org.shadow.config.model.StochasticOscillatorExplorerConfiguration;

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
        config.getConfigList("robots").stream().map(Config::parseRobotConfig).toList();

    return new Config(robotConfigs, exchangeConfig);
  }

  private static RobotConfiguration parseRobotConfig(com.typesafe.config.Config robotConfig) {
    var type = RobotType.valueOf(robotConfig.getString("type"));
    var unit = TimeUnit.valueOf(robotConfig.getString("unit"));
    var interval = robotConfig.getLong("interval");
    var symbol = robotConfig.getString("symbol");

    var orderConfig =
        new OrderConfiguration(
            robotConfig
                .getConfig("orderConfiguration")
                .getDouble("allowedOrderPercentageFromDeposit"),
            robotConfig.getConfig("orderConfiguration").getInt("allowedOrderFuturesMultiplier"),
            robotConfig.getConfig("orderConfiguration").getDouble("stopLossRequiredPercentage"));

    var historicalDataFile =
        robotConfig.hasPath("historicalDataFile")
            ? Optional.of(robotConfig.getString("historicalDataFile"))
            : Optional.<String>empty();

    // Parse multipliers
    var notReadyMultiplier = robotConfig.getInt("notReadyMultiplier");
    var minorMultiplier = robotConfig.getInt("minorMultiplier");
    var mediumMultiplier = robotConfig.getInt("mediumMultiplier");
    var majorMultiplier = robotConfig.getInt("majorMultiplier");

    // Parse explorer configurations
    var rsiExplorerConfig = parseRSIExplorerConfig(robotConfig.getConfig("rsiExplorerConfig"));
    var macdExplorerConfig = parseMACDExplorerConfig(robotConfig.getConfig("macdExplorerConfig"));
    var bollingerExplorerConfig =
        parseBollingerBandsExplorerConfig(robotConfig.getConfig("bollingerBandsExplorerConfig"));
    var stochasticExplorerConfig =
        parseStochasticOscillatorExplorerConfig(
            robotConfig.getConfig("stochasticOscillatorExplorerConfig"));

    return new RobotConfiguration(
        type,
        unit,
        interval,
        symbol,
        orderConfig,
        historicalDataFile,
        rsiExplorerConfig,
        macdExplorerConfig,
        bollingerExplorerConfig,
        stochasticExplorerConfig,
        notReadyMultiplier,
        minorMultiplier,
        mediumMultiplier,
        majorMultiplier);
  }

  private static RSIExplorerConfiguration parseRSIExplorerConfig(
      com.typesafe.config.Config config) {
    return new RSIExplorerConfiguration(
        config.getInt("severity"),
        config.getInt("period"),
        config.getDouble("oversoldThreshold"),
        config.getDouble("overboughtThreshold"),
        config.getDouble("longMediumThreshold"),
        config.getDouble("shortMediumThreshold"),
        config.getDouble("longMinorThreshold"),
        config.getDouble("shortMinorThreshold"));
  }

  private static MACDExplorerConfiguration parseMACDExplorerConfig(
      com.typesafe.config.Config config) {
    return new MACDExplorerConfiguration(
        config.getInt("severity"),
        config.getInt("shortPeriod"),
        config.getInt("longPeriod"),
        config.getInt("signalPeriod"),
        config.getDouble("histogramMajorThreshold"),
        config.getDouble("histogramMediumThreshold"),
        config.getDouble("histogramMinorThreshold"));
  }

  private static BollingerBandsExplorerConfiguration parseBollingerBandsExplorerConfig(
      com.typesafe.config.Config config) {
    return new BollingerBandsExplorerConfiguration(
        config.getInt("severity"),
        config.getInt("period"),
        config.getDouble("standardDeviationMultiplier"),
        config.getDouble("lowerBandThreshold"),
        config.getDouble("upperBandThreshold"),
        config.getDouble("longMediumThreshold"),
        config.getDouble("shortMediumThreshold"),
        config.getDouble("longMinorThreshold"),
        config.getDouble("shortMinorThreshold"));
  }

  private static StochasticOscillatorExplorerConfiguration parseStochasticOscillatorExplorerConfig(
      com.typesafe.config.Config config) {
    return new StochasticOscillatorExplorerConfiguration(
        config.getInt("severity"),
        config.getInt("period"),
        config.getInt("dPeriod"),
        config.getDouble("oversoldThreshold"),
        config.getDouble("overboughtThreshold"),
        config.getDouble("longMediumThreshold"),
        config.getDouble("shortMediumThreshold"),
        config.getDouble("longMinorThreshold"),
        config.getDouble("shortMinorThreshold"));
  }
}
