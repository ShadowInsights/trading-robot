package org.shadow.config.model;

public record RSIExplorerConfiguration(
    Integer severity,
    Integer period,
    Double oversoldThreshold,
    Double overboughtThreshold,
    Double longMediumThreshold,
    Double shortMediumThreshold,
    Double longMinorThreshold,
    Double shortMinorThreshold) {}
