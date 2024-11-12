package org.shadow.config.model;

import java.math.BigDecimal;
import java.util.Optional;

public record ExchangeConfiguration(
    ExchangeConfigurationType type,
    String apiKey,
    String apiSecret,
    Optional<BigDecimal> virtualAccountInitialBalance) {}
