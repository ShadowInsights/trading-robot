package org.shadow.application.robot.indicator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.shadow.application.robot.indicator.exception.InsufficientDataException;

import static org.junit.jupiter.api.Assertions.*;

class MACDIndicatorTest {

    private MACDIndicator indicator;

    @BeforeEach
    void setup() {
        // Initialize the MACDIndicator with standard periods (12, 26, 9) before each test
        indicator = new MACDIndicator(12, 26, 9);
    }

    @Test
    void testMACDAndSignalLineConvergence() {
        // Test the convergence of MACD and signal line with increasing prices
        double[] prices = {100.0, 102.0, 104.0, 106.0};
        for (double price : prices) {
            indicator.addPrice(price);
        }

        // Check if MACD histogram is greater than the signal line, indicating a positive trend
        double macdValue = indicator.getMACDHistogram();
        double signalValue = indicator.getSignalLine();

        assertTrue(macdValue > signalValue, "MACD histogram should be greater than the signal line during upward trend.");
    }

    @Test
    void testMACDWithDecreasingPrices() {
        // Test MACD behavior with a sequence of decreasing prices
        double[] prices = {110.0, 109.0, 108.0, 107.0};
        for (double price : prices) {
            indicator.addPrice(price);
        }

        // Verify that the MACD histogram is negative, reflecting the decreasing price trend
        assertTrue(
                indicator.getMACDHistogram() < 0,
                "MACD should be negative with decreasing prices."
        );
    }

    @Test
    void testMACDWithIncreasingPrices() {
        // Test MACD behavior with a sequence of increasing prices
        double[] increasingPrices = {100.0, 101.0, 102.0, 103.0, 104.0, 105.0, 106.0, 107.0, 108.0, 109.0, 110.0};
        for (double price : increasingPrices) {
            indicator.addPrice(price);
        }

        // Verify that the MACD histogram is positive, indicating a strong upward trend
        assertTrue(indicator.getMACDHistogram() > 0, "MACD Histogram should be positive with increasing prices.");
    }

    @Test
    void testMACDWithStablePrices() {
        // Test MACD behavior with stable prices (no change)
        double[] prices = {100.0, 100.0, 100.0};
        for (double price : prices) {
            indicator.addPrice(price);
        }

        // Verify that MACD histogram is near zero, as no price change is detected
        assertEquals(
                0,
                indicator.getMACDHistogram(),
                1e-6,
                "Histogram should be near zero with stable prices."
        );
    }

    @Test
    void testMACDCalculationWithSinglePrice() {
        // Test behavior when only a single price is added
        indicator.addPrice(100.0);

        // Verify that MACD, signal line, and histogram have not changed with insufficient data
        assertEquals(0, indicator.getMACDHistogram(), "Histogram should be 0 with only one price input.");
    }

    @Test
    void testGetShortEMA() {
        // Test short EMA calculation with multiple price updates
        indicator.addPrice(100.0);
        assertEquals(100.0, indicator.getShortEMA(), "Short EMA should be initialized to the first price.");

        indicator.addPrice(102.0);
        // Short EMA should be updated based on the second price
        assertEquals(100.3076923076923, indicator.getShortEMA(), 1e-6, "Short EMA should be updated correctly.");
    }

    @Test
    void testGetLongEMA() {
        // Test long EMA calculation with multiple price updates
        indicator.addPrice(100.0);
        assertEquals(100.0, indicator.getLongEMA(), "Long EMA should be initialized to the first price.");

        indicator.addPrice(102.0);
        // Long EMA should be updated based on the second price
        assertEquals(100.14814814814815, indicator.getLongEMA(), 1e-6, "Long EMA should be updated correctly.");
    }

    @Test
    void testInsufficientDataForMACD() {
        // Test that an exception is thrown if MACD is accessed with insufficient data
        assertThrows(InsufficientDataException.class, () -> indicator.getMACDHistogram());
    }

    @Test
    void testInsufficientDataForSignalLine() {
        // Test that an exception is thrown if the signal line is accessed with insufficient data
        assertThrows(InsufficientDataException.class, () -> indicator.getSignalLine());
    }

}
