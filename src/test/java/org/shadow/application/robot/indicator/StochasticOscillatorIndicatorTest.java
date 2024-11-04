package org.shadow.application.robot.indicator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.indicator.exception.InsufficientDataException;

class StochasticOscillatorIndicatorTest {

  @Test
  void testCalculate() {
    var bars = createBars();

    var period = 14;
    var dPeriod = 3;

    var indicator = new StochasticOscillatorIndicator(period, dPeriod);

    var result = indicator.calculate(bars);

    var expectedK = 78.26086956521739;
    var expectedD = 78.26086956521739;

    assertEquals(result.percentK(), expectedK);
    assertEquals(result.percentD(), expectedD);
  }

  @Test
  void testInsufficientData() {
    var bars = new ArrayList<Bar>();
    var indicator = new StochasticOscillatorIndicator(14, 3);

    Assertions.assertThrows(
        InsufficientDataException.class,
        () -> {
          indicator.calculate(bars);
        });
  }

  private List<Bar> createBars() {
    var bars = new ArrayList<Bar>();
    for (var i = 0; i < 20; i++) {
      var high = BigDecimal.valueOf(100 + i);
      var low = BigDecimal.valueOf(90 + i);
      var close = BigDecimal.valueOf(95 + i);

      var bar = new Bar(null, null, high, low, close, null);
      bars.add(bar);
    }
    return bars;
  }
}
