package org.shadow.config.model;

public record OrderConfiguration(
    double allowedOrderPercentageFromDeposit,
    int allowedOrderFuturesMultiplier,
    double stopLossRequiredPercentage) {}
