package org.shadow.config.model;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public record RobotConfiguration(
    RobotType type,
    TimeUnit unit,
    long interval,
    String symbol,
    OrderConfiguration orderConfiguration,
    Optional<String> historicalDataFile,
    RSIExplorerConfiguration rsiExplorerConfig,
    MACDExplorerConfiguration macdExplorerConfig,
    BollingerBandsExplorerConfiguration bollingerBandsExplorerConfig,
    StochasticOscillatorExplorerConfiguration stochasticOscillatorExplorerConfig,
    int notReadyMultiplier,
    int minorMultiplier,
    int mediumMultiplier,
    int majorMultiplier) {}
