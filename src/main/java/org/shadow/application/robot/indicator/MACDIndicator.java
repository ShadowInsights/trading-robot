package org.shadow.application.robot.indicator;

import org.shadow.application.robot.indicator.exception.InsufficientDataException;

/**
 * MACDIndicator calculates the Moving Average Convergence Divergence (MACD) for a set of prices.
 * The MACD is derived from the difference between a short-term and a long-term Exponential Moving Average (EMA),
 * followed by the calculation of a signal line, which is an EMA of the MACD itself.
 */
public class MACDIndicator {

    private final int shortPeriod;
    private final int longPeriod;
    private final int signalPeriod;

    private Double shortEMA = null;  // Last value of the short EMA
    private Double longEMA = null;   // Last value of the long EMA
    private Double signalEMA = null; // Last value of the MACD signal line
    private Double macd = null;      // Last value of the MACD

    /**
     * Constructs a MACDIndicator with the specified periods for the short EMA, long EMA, and signal line.
     *
     * @param shortPeriod  The period for the short-term EMA.
     * @param longPeriod   The period for the long-term EMA.
     * @param signalPeriod The period for the signal line EMA.
     */
    public MACDIndicator(int shortPeriod, int longPeriod, int signalPeriod) {
        if (shortPeriod <= 0 || longPeriod <= 0 || signalPeriod <= 0) {
            throw new IllegalArgumentException("Period values must be positive.");
        }
        this.shortPeriod = shortPeriod;
        this.longPeriod = longPeriod;
        this.signalPeriod = signalPeriod;
    }

    /**
     * Adds a new price and recalculates the MACD and the signal line.
     *
     * @param price The new price to be added for recalculating the MACD.
     */
    public void addPrice(double price) {
        if (shortEMA == null || longEMA == null) {
            initializeEMAs(price);
        } else {
            shortEMA = calculateEMA(price, shortEMA, shortPeriod);
            longEMA = calculateEMA(price, longEMA, longPeriod);
        }

        macd = shortEMA - longEMA;
        signalEMA = calculateEMA(macd, signalEMA, signalPeriod);
    }

    /**
     * Initializes the short and long EMAs with the first price input.
     *
     * @param price The initial price to set the EMA values.
     */
    private void initializeEMAs(double price) {
        shortEMA = price;
        longEMA = price;
        macd = 0.0;
        signalEMA = 0.0;
    }

    /**
     * Generic method for calculating the Exponential Moving Average (EMA).
     *
     * @param price   The current price to be factored into the EMA calculation.
     * @param prevEMA The previous EMA value.
     * @param period  The period over which to calculate the EMA.
     * @return The updated EMA value.
     */
    private double calculateEMA(double price, double prevEMA, int period) {
        double alpha = 2.0 / (period + 1);
        return price * alpha + prevEMA * (1 - alpha);
    }

    /**
     * Returns the MACD histogram, which is the difference between the MACD and the signal line.
     * The histogram is used to identify the strength of price movements.
     *
     * @return The MACD histogram or throws InsufficientDataException if not enough data is available.
     */
    public double getMACDHistogram() {
        if (macd == null || signalEMA == null) {
            throw new InsufficientDataException("Not enough data to calculate MACD histogram.");
        }
        return macd - signalEMA;
    }

    /**
     * Returns the current signal line value.
     *
     * @return The current signal line value.
     * @throws InsufficientDataException if the signal line has not been initialized yet.
     */
    public double getSignalLine() {
        if (signalEMA == null) {
            throw new InsufficientDataException("Signal line is not available yet.");
        }
        return signalEMA;
    }

    /**
     * Returns the current short-term EMA value.
     *
     * @return The current short-term EMA value.
     * @throws InsufficientDataException if the short-term EMA has not been initialized yet.
     */
    public double getShortEMA() {
        if (shortEMA == null) {
            throw new InsufficientDataException("Short EMA is not available yet.");
        }
        return shortEMA;
    }

    /**
     * Returns the current long-term EMA value.
     *
     * @return The current long-term EMA value.
     * @throws InsufficientDataException if the long-term EMA has not been initialized yet.
     */
    public double getLongEMA() {
        if (longEMA == null) {
            throw new InsufficientDataException("Long EMA is not available yet.");
        }
        return longEMA;
    }
}
