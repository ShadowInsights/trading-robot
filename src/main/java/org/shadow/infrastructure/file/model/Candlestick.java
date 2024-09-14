package org.shadow.infrastructure.file.model;

public record Candlestick(
    long timestamp, String open, String high, String low, String close, String volume) {}
