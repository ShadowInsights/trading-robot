package org.shadow.config.model;

public record StochasticOscillatorExplorerConfiguration(
    Integer severity,
    Integer period,
    Integer dPeriod,
    Double oversoldThreshold,
    Double overboughtThreshold,
    Double longMediumThreshold,
    Double shortMediumThreshold,
    Double longMinorThreshold,
    Double shortMinorThreshold) {}
