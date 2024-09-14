package org.shadow.application.robot.blocker;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.shadow.application.robot.common.model.Bar;

class ATRBlockerTest {

  @Test
  void testLowVolatilityBlocking() {
    var bars = generateBarsWithConstantPrice();
    var atrBlocker = new ATRBlocker(7);
    var shouldBlock = atrBlocker.isMomentumToBlocking(bars);
    assertTrue(shouldBlock, "Trading should be blocked due to low volatility.");
  }

  @Test
  void testHighVolatilityBlocking() {
    var bars = generateBarsWithHighVolatility();
    var atrBlocker = new ATRBlocker(7);
    var shouldBlock = atrBlocker.isMomentumToBlocking(bars);
    assertTrue(shouldBlock, "Trading should be blocked due to high volatility.");
  }

  @Test
  void testNormalVolatilityNotBlocking() {
    var bars = generateBarsWithNormalVolatility(7);
    var atrBlocker = new ATRBlocker(7);
    var shouldBlock = atrBlocker.isMomentumToBlocking(bars);
    assertFalse(shouldBlock, "Trading should not be blocked under normal volatility.");
  }

  @Test
  void testInsufficientData() {
    var bars = generateBarsWithNormalVolatility(5);
    var atrBlocker = new ATRBlocker(7);
    var shouldBlock = atrBlocker.isMomentumToBlocking(bars);
    assertTrue(shouldBlock, "Trading should be blocked due to insufficient data.");
  }

  @Test
  void testEmptyBarsList() {
    var bars = new ArrayList<Bar>();
    var atrBlocker = new ATRBlocker(7);
    var shouldBlock = atrBlocker.isMomentumToBlocking(bars);
    assertTrue(shouldBlock, "Trading should be blocked when bars list is empty.");
  }

  @Test
  void testNullBarsList() {
    var atrBlocker = new ATRBlocker(7);
    var shouldBlock = atrBlocker.isMomentumToBlocking(null);
    assertTrue(shouldBlock, "Trading should be blocked when bars list is null.");
  }

  private java.util.List<Bar> generateBarsWithConstantPrice() {
    var bars = new ArrayList<Bar>();
    for (int i = 0; i < 7; i++) {
      bars.add(createBar(100.0, 100.0, 100.0, 100.0));
    }
    return bars;
  }

  private java.util.List<Bar> generateBarsWithHighVolatility() {
    var bars = new ArrayList<Bar>();
    var price = 100.0;
    for (int i = 0; i < 7; i++) {
      var high = price + (price * 0.3 / 100);
      var low = price - (price * 0.3 / 100);
      var close = price + (Math.random() - 0.5) * (price * 0.6 / 100);
      bars.add(createBar(price, high, low, close));
      price = close;
    }
    return bars;
  }

  private java.util.List<Bar> generateBarsWithNormalVolatility(int count) {
    var bars = new ArrayList<Bar>();
    var price = 100.0;
    for (int i = 0; i < count; i++) {
      var high = price + (price * 0.1 / 100);
      var low = price - (price * 0.1 / 100);
      var close = price + (Math.random() - 0.5) * (price * 0.2 / 100);
      bars.add(createBar(price, high, low, close));
      price = close;
    }
    return bars;
  }

  private Bar createBar(double open, double high, double low, double close) {
    return new Bar(
        Instant.now(),
        BigDecimal.valueOf(open),
        BigDecimal.valueOf(high),
        BigDecimal.valueOf(low),
        BigDecimal.valueOf(close),
        BigDecimal.ZERO);
  }
}
