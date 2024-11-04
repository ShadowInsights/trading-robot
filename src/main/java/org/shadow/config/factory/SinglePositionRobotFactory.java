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

  // TODO: Make constants below configurable
  public static final int NOT_READY_MULTIPLIER = 0;
  public static final int MINOR_MULTIPLIER = 1;
  public static final int MEDIUM_MULTIPLIER = 2;
  public static final int MAJOR_MULTIPLIER = 3;

  // Constants for RSI Explorer
  public static final int RSI_EXPLORER_SEVERITY = 1;
  public static final int RSI_EXPLORER_PERIOD = 7;

  // Constants for MACD Explorer
  public static final int MACD_EXPLORER_SEVERITY = 1;
  public static final int MACD_SHORT_PERIOD = 12;
  public static final int MACD_LONG_PERIOD = 26;
  public static final int MACD_SIGNAL_PERIOD = 9;

  // Constants for Bollinger Bands Explorer
  public static final int BOLLINGER_EXPLORER_SEVERITY = 1;
  public static final int BOLLINGER_PERIOD = 20;
  public static final double BOLLINGER_STD_DEV_MULTIPLIER = 2.0;

  // Constants for Stochastic Oscillator Explorer
  public static final int STOCHASTIC_EXPLORER_SEVERITY = 1;
  public static final int STOCHASTIC_PERIOD = 14;
  public static final int STOCHASTIC_D_PERIOD = 3;

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

    var stateMultiplierMap =
        new EnumMap<BinaryIsMomentumExplorationState, Integer>(
            BinaryIsMomentumExplorationState.class);

    stateMultiplierMap.put(BinaryIsMomentumExplorationState.NOT_READY, NOT_READY_MULTIPLIER);
    stateMultiplierMap.put(BinaryIsMomentumExplorationState.MINOR, MINOR_MULTIPLIER);
    stateMultiplierMap.put(BinaryIsMomentumExplorationState.MEDIUM, MEDIUM_MULTIPLIER);
    stateMultiplierMap.put(BinaryIsMomentumExplorationState.MAJOR, MAJOR_MULTIPLIER);

    var binaryExplorers =
        List.of(
            new RSIBinaryExplorer(RSI_EXPLORER_SEVERITY, RSI_EXPLORER_PERIOD),
            new MACDBinaryExplorer(
                MACD_EXPLORER_SEVERITY, MACD_SHORT_PERIOD, MACD_LONG_PERIOD, MACD_SIGNAL_PERIOD),
            new BollingerBandsBinaryExplorer(
                BOLLINGER_EXPLORER_SEVERITY, BOLLINGER_PERIOD, BOLLINGER_STD_DEV_MULTIPLIER),
            new StochasticOscillatorExplorer(
                STOCHASTIC_EXPLORER_SEVERITY, STOCHASTIC_PERIOD, STOCHASTIC_D_PERIOD));

    var blockers = List.<Blocker>of(new ATRBlocker(ATR_BLOCKER_PERIOD));

    var binaryStrategy =
        new BinaryStrategy(
            binaryExplorers, blockers, stopLossRequiredPercentage, stateMultiplierMap);

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
        percentagePerDeposit,
        futuresMultiplier,
        Instant.ofEpochMilli(initialBarsCollectionDate),
        maximumRequiredPeriodThreshold);
  }
}
