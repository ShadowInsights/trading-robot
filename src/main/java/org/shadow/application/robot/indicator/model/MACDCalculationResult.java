package org.shadow.application.robot.indicator.model;

/** Holds the MACD line and signal line values. */
public record MACDCalculationResult(double macdLine, double signalLine) {

  /**
   * Calculates the histogram value, which is the difference between the MACD line and the signal
   * line.
   *
   * @return The histogram value.
   */
  public double getHistogram() {
    return macdLine - signalLine;
  }
}
