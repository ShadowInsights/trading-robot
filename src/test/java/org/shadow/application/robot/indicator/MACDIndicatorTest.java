package org.shadow.application.robot.indicator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MACDIndicatorTest {

    private MACDIndicator indicator;

    @BeforeEach
    void setup() {
        indicator = new MACDIndicator(12, 26, 9);
    }

    @Test
    void testMACDWithDecreasingPrices() {
        // Add a sequence of decreasing prices
        double[] prices = {110, 109, 108, 107};
        for (double price : prices) {
            indicator.addPrice(price);
        }

        // Verify that MACD and signal line have negative values
        assertTrue(
                indicator.getMACDHistogram() < 0,
                "MACD should be negative with decreasing prices."
        );
    }

    @Test
    void testMACDWithIncreasingPrices() {
        // Add a sequence of increasing prices
        double[] increasingPrices = {100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110};
        for (double price : increasingPrices) {
            indicator.addPrice(price);
        }

        // Verify that the MACD histogram is positive (if MACD is growing faster than the signal line)
        assertTrue(indicator.getMACDHistogram() > 0, "MACD Histogram should be positive with increasing prices.");
    }

    @Test
    void testMACDWithStablePrices() {
        // Add stable prices to ensure that MACD stabilizes
        double[] prices = {100, 100, 100, 100, 100, 100, 100, 100, 100, 100};
        for (double price : prices) {
            indicator.addPrice(price);
        }

        // Verify that MACD is close to zero with stable prices
        assertEquals(
                0,
                indicator.getMACDHistogram(),
                1e-6,
                "Histogram should be near zero with stable prices."
        );
    }

    @Test
    void testMACDCalculationWithSinglePrice() {
        // Add a single price and verify that MACD, signal line, and histogram have not changed
        indicator.addPrice(100.0);
        assertEquals(0, indicator.getMACDHistogram());
    }

}
