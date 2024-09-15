package org.shadow.config.factory;

import static org.shadow.config.util.TimeUtil.calculateShiftBackToPreviousPeriod;

import java.time.Instant;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Stream;
import org.shadow.application.robot.RobotTimeframe;
import org.shadow.application.robot.SinglePositionRobot;
import org.shadow.application.robot.blocker.ATRBlocker;
import org.shadow.application.robot.blocker.Blocker;
import org.shadow.application.robot.explorer.BinaryExplorer;
import org.shadow.application.robot.explorer.RSIBinaryExplorer;
import org.shadow.application.robot.explorer.model.BinaryIsMomentumExplorationState;
import org.shadow.application.robot.strategy.BinaryStrategy;
import org.shadow.config.model.RobotConfiguration;
import org.shadow.domain.client.BarsCollectorClient;
import org.shadow.domain.client.ExchangeOrderClient;

public class SinglePositionRobotFactory implements RobotFactory<SinglePositionRobot> {

  // TODO: Make constants below configurable
  public static final int NOT_READY_MULTIPLIER = 0;
  public static final int MINOR_MULTIPLIER = 1;
  public static final int MEDIUM_MULTIPLIER = 2;
  public static final int MAJOR_MULTIPLIER = 3;

  public static final int RSI_EXPLORER_SEVERITY = 1;
  // Valid only for 1-minute timeframe
  public static final int RSI_EXPLORER_PERIOD = 7;

  // Valid only for 1-minute timeframe
  private static final int ATR_BLOCKER_PERIOD = 7;

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

    var binaryIsMomentumExplorationStateIntegerMultiplierMap =
        new EnumMap<BinaryIsMomentumExplorationState, Integer>(
            BinaryIsMomentumExplorationState.class);

    binaryIsMomentumExplorationStateIntegerMultiplierMap.put(
        BinaryIsMomentumExplorationState.NOT_READY, NOT_READY_MULTIPLIER);
    binaryIsMomentumExplorationStateIntegerMultiplierMap.put(
        BinaryIsMomentumExplorationState.MINOR, MINOR_MULTIPLIER);
    binaryIsMomentumExplorationStateIntegerMultiplierMap.put(
        BinaryIsMomentumExplorationState.MEDIUM, MEDIUM_MULTIPLIER);
    binaryIsMomentumExplorationStateIntegerMultiplierMap.put(
        BinaryIsMomentumExplorationState.MAJOR, MAJOR_MULTIPLIER);

    var binaryExplorers =
        List.<BinaryExplorer>of(new RSIBinaryExplorer(RSI_EXPLORER_SEVERITY, RSI_EXPLORER_PERIOD));
    var blockers = List.<Blocker>of(new ATRBlocker(ATR_BLOCKER_PERIOD));
    var binaryStrategy =
        new BinaryStrategy(
            binaryExplorers,
            blockers,
            stopLossRequiredPercentage,
            binaryIsMomentumExplorationStateIntegerMultiplierMap);

    var maximumRequiredPeriodThreshold =
        Stream.concat(
                binaryExplorers.stream()
                    .map(
                        binaryExplorer ->
                            binaryExplorer.getIndicator().getRequiredPeriodThreshold()),
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
        percentagePerDeposit,
        futuresMultiplier,
        Instant.ofEpochMilli(initialBarsCollectionDate),
        maximumRequiredPeriodThreshold);
  }
}
