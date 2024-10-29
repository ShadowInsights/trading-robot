package org.shadow.application.robot.indicator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.shadow.application.robot.indicator.exception.InsufficientDataException;

class MACDIndicatorTest {

  private MACDIndicator macdIndicator;

  @BeforeEach
  void setup() {
    macdIndicator = new MACDIndicator(12, 26, 9); // Using typical MACD periods
  }

  @Test
  void testConstructorWithInvalidPeriods() {
    assertThrows(IllegalArgumentException.class, () -> new MACDIndicator(-12, 26, 9));
    assertThrows(IllegalArgumentException.class, () -> new MACDIndicator(12, -26, 9));
    assertThrows(IllegalArgumentException.class, () -> new MACDIndicator(12, 26, -9));
    assertThrows(IllegalArgumentException.class, () -> new MACDIndicator(0, 26, 9));
  }

  @Test
  void testCalculateWithSufficientData() {
    var prices = new double[]{
      100.0, 101.0, 102.0, 103.0, 104.0, 105.0, 106.0, 107.0, 108.0, 109.0, 110.0, 111.0, 112.0,
      113.0, 114.0, 115.0, 116.0, 117.0, 118.0, 119.0, 120.0, 121.0, 122.0, 123.0, 124.0, 125.0,
      126.0, 127.0, 128.0, 129.0, 130.0, 131.0, 132.0, 133.0, 134.0, 135.0
    };

    var result = macdIndicator.calculate(prices);
    assertNotNull(result);
  }

  @Test
  void testCalculateWithInsufficientData() {
    var prices = new double[]{100.0, 101.0, 102.0, 103.0, 104.0, 105.0};
    assertThrows(InsufficientDataException.class, () -> macdIndicator.calculate(prices));
  }

  @Test
  void testCalculateWithExactlyLongPeriodData() {
    var prices = new double[]{
      100.0, 101.0, 102.0, 103.0, 104.0, 105.0, 106.0, 107.0, 108.0, 109.0, 110.0, 111.0, 112.0,
      113.0, 114.0, 115.0, 116.0, 117.0, 118.0, 119.0, 120.0, 121.0, 122.0, 123.0, 124.0, 125.0
    };

    assertThrows(InsufficientDataException.class, () -> macdIndicator.calculate(prices));
  }

  @Test
  void testCalculateExactMACDValue() {
    var prices = new double[]{
      100.0, 101.0, 102.0, 103.0, 104.0, 105.0, 106.0, 107.0, 108.0, 109.0,
      110.0, 111.0, 112.0, 113.0, 114.0, 115.0, 116.0, 117.0, 118.0, 119.0,
      120.0, 121.0, 122.0, 123.0, 124.0, 125.0, 126.0, 127.0, 128.0, 129.0,
      130.0, 131.0, 132.0, 133.0, 134.0, 135.0, 136.0, 137.0, 138.0, 139.0,
      140.0
    };

    var expectedMACD = 1.6858140220764604;

    var result = macdIndicator.calculate(prices);

    assertEquals(expectedMACD, result.getHistogram());
  }
}
