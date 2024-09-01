package org.shadow.config.model;

import java.math.BigDecimal;

public record OrderConfiguration(
    BigDecimal allowedOrderPercentageFromDeposit,
    Integer allowedOrderFuturesMultiplier,
    BigDecimal stopLossRequiredPercentage) {}
