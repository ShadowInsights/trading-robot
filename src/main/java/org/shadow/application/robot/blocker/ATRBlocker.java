package org.shadow.application.robot.blocker;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.indicator.ATRIndicator;

public class ATRBlocker implements Blocker {

  private static final double ATR_THRESHOLD = 0.5;
  private final ATRIndicator atrIndicator;
  private final Logger logger = LogManager.getLogger(ATRBlocker.class);

  public ATRBlocker(int atrPeriod) {
    this.atrIndicator = new ATRIndicator(atrPeriod);
    logger.info("ATRBlocker initialized with ATR period: {}", atrPeriod);
  }

  @Override
  public boolean isMomentumToBlocking(List<Bar> bars) {
    if (bars == null || bars.size() < 2) {
      logger.error(
          "Not enough bars to calculate ATR. Bars size: {}", (bars == null ? "null" : bars.size()));
      throw new IllegalArgumentException("Not enough bars to calculate ATR.");
    }

    logger.info("Calculating ATR for {} bars.", bars.size());

    var highs = bars.stream().mapToDouble(bar -> bar.high().doubleValue()).toArray();
    var lows = bars.stream().mapToDouble(bar -> bar.low().doubleValue()).toArray();
    var closes = bars.stream().mapToDouble(bar -> bar.close().doubleValue()).toArray();

    var atrValues = atrIndicator.calculate(highs, lows, closes);
    var lastATR = atrValues.getLast();

    logger.info("Calculated ATR values: {}", atrValues);
    logger.info("Last ATR value: {}", lastATR);
    logger.info("ATR Threshold: {}", ATR_THRESHOLD);

    if (lastATR > ATR_THRESHOLD) {
      logger.warn("High volatility detected. Blocking position opening.");
      return true;
    }

    logger.info("Volatility is within acceptable range. No blocking.");
    return false;
  }
}
