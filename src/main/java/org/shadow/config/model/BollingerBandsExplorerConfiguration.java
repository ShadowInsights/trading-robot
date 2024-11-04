package org.shadow.config.model;

public record BollingerBandsExplorerConfiguration(
    Integer severity,
    Integer period,
    Double standardDeviationMultiplier,
    Double lowerBandThreshold,
    Double upperBandThreshold,
    Double longMediumThreshold,
    Double shortMediumThreshold,
    Double longMinorThreshold,
    Double shortMinorThreshold) {}
