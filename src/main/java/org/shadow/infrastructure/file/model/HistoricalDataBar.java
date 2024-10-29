package org.shadow.infrastructure.file.model;

public record HistoricalDataBar(
    long timestamp, String open, String high, String low, String close, String volume) {}
