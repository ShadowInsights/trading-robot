package org.shadow.config.model;

import java.util.concurrent.TimeUnit;

public record RobotConfiguration(
    RobotType type,
    TimeUnit unit,
    long interval,
    String symbol,
    OrderConfiguration orderConfiguration) {}
