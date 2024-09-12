package org.shadow.application.robot.indicator;

public class MACDIndicator {

    private final int shortPeriod;
    private final int longPeriod;
    private final int signalPeriod;

    private Double shortEMA = null;  // Last value of the short EMA
    private Double longEMA = null;   // Last value of the long EMA
    private Double signalEMA = null; // Last value of the MACD signal line
    private Double macd = null;      // Last value of the MACD

    public MACDIndicator(int shortPeriod, int longPeriod, int signalPeriod) {
        this.shortPeriod = shortPeriod;
        this.longPeriod = longPeriod;
        this.signalPeriod = signalPeriod;
    }

    // Method for adding a new price and recalculating MACD
    public void addPrice(double price) {
        updateEMA(price);
    }

    // Method for updating the short and long EMAs, as well as the signal line
    private void updateEMA(double price) {
        // If the EMAs have not been calculated yet, initialize them with the first price
        if (shortEMA == null) {
            shortEMA = price;
            longEMA = price;
        } else {
            double shortAlpha = 2.0 / (shortPeriod + 1);
            double longAlpha = 2.0 / (longPeriod + 1);

            // Update the short and long EMAs
            shortEMA = price * shortAlpha + shortEMA * (1 - shortAlpha);
            longEMA = price * longAlpha + longEMA * (1 - longAlpha);
        }

        // Calculate MACD (the difference between the short and long EMAs)
        macd = shortEMA - longEMA;

        // Update the MACD signal line (EMA of the MACD value)
        if (signalEMA == null) {
            signalEMA = macd;  // Initialize the MACD signal line
        } else {
            double signalAlpha = 2.0 / (signalPeriod + 1);
            signalEMA = macd * signalAlpha + signalEMA * (1 - signalAlpha);
        }
    }

    // Method for getting the MACD histogram (difference between MACD and the signal line)
    // TODO Add normalization for range [-100, 100]. Check average range
    // TODO Check if there's necessarity for adaptive timeframe
    public double getMACDHistogram() {
        if (macd == null || signalEMA == null) {
            return 0;  // Not enough data
        }
        return macd - signalEMA;
    }
}
