package org.shadow.application.robot.indicator.model;

/** Holds the Bollinger Bands calculation result: upper band, middle band, and lower band. */
public record BollingerBandsResult(double upperBand, double middleBand, double lowerBand) {}
