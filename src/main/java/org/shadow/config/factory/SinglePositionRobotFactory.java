package org.shadow.config.factory;

import static org.shadow.config.util.TimeUtil.calculateShiftBackToPreviousPeriod;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Stream;
import org.shadow.application.robot.RobotTimeframe;
import org.shadow.application.robot.SinglePositionRobot;
import org.shadow.application.robot.blocker.ATRBlocker;
import org.shadow.application.robot.blocker.Blocker;
import org.shadow.application.robot.explorer.BollingerBandsBinaryExplorer;
import org.shadow.application.robot.explorer.MACDBinaryExplorer;
import org.shadow.application.robot.explorer.RSIBinaryExplorer;
import org.shadow.application.robot.explorer.StochasticOscillatorExplorer;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;
import org.shadow.application.robot.strategy.BinaryStrategy;
import org.shadow.config.model.RobotConfiguration;
import org.shadow.domain.client.BarsCollectorClient;
import org.shadow.domain.client.ExchangeOrderClient;

public class SinglePositionRobotFactory implements RobotFactory<SinglePositionRobot> {

  @Override
  public SinglePositionRobot createRobot(
      RobotConfiguration robotConfiguration,
      BarsCollectorClient barsCollectorClient,
      ExchangeOrderClient exchangeOrderClient) {

    var robotTimeframe =
        new RobotTimeframe(robotConfiguration.unit(), robotConfiguration.interval());

    var symbol = robotConfiguration.symbol();
    var percentagePerDeposit =
        robotConfiguration.orderConfiguration().allowedOrderPercentageFromDeposit();
    var futuresMultiplier = robotConfiguration.orderConfiguration().allowedOrderFuturesMultiplier();
    var stopLossRequiredPercentage =
        robotConfiguration.orderConfiguration().stopLossRequiredPercentage();

    var stateMultiplierMap =
        new EnumMap<BinaryIsMomentumExplorationState, Integer>(
            BinaryIsMomentumExplorationState.class);

    stateMultiplierMap.put(
        BinaryIsMomentumExplorationState.NOT_READY, robotConfiguration.notReadyMultiplier());
    stateMultiplierMap.put(
        BinaryIsMomentumExplorationState.MINOR, robotConfiguration.minorMultiplier());
    stateMultiplierMap.put(
        BinaryIsMomentumExplorationState.MEDIUM, robotConfiguration.mediumMultiplier());
    stateMultiplierMap.put(
        BinaryIsMomentumExplorationState.MAJOR, robotConfiguration.majorMultiplier());

    var rsiConfig = robotConfiguration.rsiExplorerConfig();
    var macdConfig = robotConfiguration.macdExplorerConfig();
    var bollingerConfig = robotConfiguration.bollingerBandsExplorerConfig();
    var stochasticConfig = robotConfiguration.stochasticOscillatorExplorerConfig();

    var binaryExplorers =
        List.of(
            new RSIBinaryExplorer(
                rsiConfig.severity(),
                rsiConfig.period(),
                rsiConfig.oversoldThreshold(),
                rsiConfig.overboughtThreshold(),
                rsiConfig.longMediumThreshold(),
                rsiConfig.shortMediumThreshold(),
                rsiConfig.longMinorThreshold(),
                rsiConfig.shortMinorThreshold()),
            new MACDBinaryExplorer(
                macdConfig.severity(),
                macdConfig.shortPeriod(),
                macdConfig.longPeriod(),
                macdConfig.signalPeriod(),
                macdConfig.histogramMajorThreshold(),
                macdConfig.histogramMediumThreshold(),
                macdConfig.histogramMinorThreshold()),
            new BollingerBandsBinaryExplorer(
                bollingerConfig.severity(),
                bollingerConfig.period(),
                bollingerConfig.standardDeviationMultiplier(),
                bollingerConfig.lowerBandThreshold(),
                bollingerConfig.upperBandThreshold(),
                bollingerConfig.longMediumThreshold(),
                bollingerConfig.shortMediumThreshold(),
                bollingerConfig.longMinorThreshold(),
                bollingerConfig.shortMinorThreshold()),
            new StochasticOscillatorExplorer(
                stochasticConfig.severity(),
                stochasticConfig.period(),
                stochasticConfig.dPeriod(),
                stochasticConfig.oversoldThreshold(),
                stochasticConfig.overboughtThreshold(),
                stochasticConfig.longMediumThreshold(),
                stochasticConfig.shortMediumThreshold(),
                stochasticConfig.longMinorThreshold(),
                stochasticConfig.shortMinorThreshold()));

    var blockers = List.<Blocker>of(new ATRBlocker(7)); // TODO: Make this configurable

    var binaryStrategy =
        new BinaryStrategy(
            binaryExplorers,
            blockers,
            BigDecimal.valueOf(stopLossRequiredPercentage),
            stateMultiplierMap);

    var maximumRequiredPeriodThreshold =
        Stream.concat(
                binaryExplorers.stream()
                    .map(explorer -> explorer.getIndicator().getRequiredPeriodThreshold()),
                blockers.stream()
                    .map(blocker -> blocker.getIndicator().getRequiredPeriodThreshold()))
            .max(Integer::compare)
            .orElse(0);

    var initialBarsCollectionDate =
        calculateShiftBackToPreviousPeriod(
            robotTimeframe.interval(), robotTimeframe.unit(), maximumRequiredPeriodThreshold);

    return new SinglePositionRobot(
        robotTimeframe,
        barsCollectorClient,
        exchangeOrderClient,
        binaryStrategy,
        symbol,
        BigDecimal.valueOf(percentagePerDeposit),
        futuresMultiplier,
        Instant.ofEpochMilli(initialBarsCollectionDate),
        maximumRequiredPeriodThreshold);
  }
}
