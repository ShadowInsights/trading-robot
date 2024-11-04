package org.shadow.application.robot.indicator.model;

/**
 * A record that holds the result of the Stochastic Oscillator calculation, containing %K and %D
 * values.
 */
public record StochasticOscillatorResult(double percentK, double percentD) {}
