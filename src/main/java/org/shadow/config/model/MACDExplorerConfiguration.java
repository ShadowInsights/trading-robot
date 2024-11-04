package org.shadow.config.model;

public record MACDExplorerConfiguration(
    Integer severity,
    Integer shortPeriod,
    Integer longPeriod,
    Integer signalPeriod,
    Double histogramMajorThreshold,
    Double histogramMediumThreshold,
    Double histogramMinorThreshold) {}
