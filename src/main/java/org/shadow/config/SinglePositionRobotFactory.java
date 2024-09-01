package org.shadow.config;

import java.util.EnumMap;
import java.util.List;
import org.shadow.application.robot.RobotTimeframe;
import org.shadow.application.robot.SinglePositionRobot;
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
  private static final int NOT_READY_MULTIPLIER = 0;
  private static final int MINOR_MULTIPLIER = 1;
  private static final int MEDIUM_MULTIPLIER = 2;
  private static final int MAJOR_MULTIPLIER = 3;

  private static final int RSI_EXPLORER_PERIOD = 1;

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

    var binaryExplorers = List.<BinaryExplorer>of(new RSIBinaryExplorer(RSI_EXPLORER_PERIOD));
    var blockers = List.<Blocker>of();
    var binaryStrategy =
        new BinaryStrategy(
            binaryExplorers,
            blockers,
            stopLossRequiredPercentage,
            binaryIsMomentumExplorationStateIntegerMultiplierMap);

    return new SinglePositionRobot(
        robotTimeframe,
        barsCollectorClient,
        exchangeOrderClient,
        binaryStrategy,
        symbol,
        percentagePerDeposit,
        futuresMultiplier);
  }
}
