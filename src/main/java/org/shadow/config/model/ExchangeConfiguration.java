package org.shadow.config.model;

public record ExchangeConfiguration(
    ExchangeConfigurationType type, String apiKey, String apiSecret) {}
