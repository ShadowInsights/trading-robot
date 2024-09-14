package org.shadow.application.robot.blocker;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.application.robot.common.model.Bar;
import org.shadow.application.robot.indicator.ATRIndicator;
import org.shadow.application.robot.indicator.exception.InsufficientDataException;

/**
 * The ATRBlocker uses the Average True Range (ATR) indicator to determine if the market is in high
 * or low volatility and decides whether to block trading accordingly.
 */
public class ATRBlocker implements Blocker {

  // TODO: Make constants below configurable
  // Valid only for 1-minute timeframe and ATR period of 7
  private static final double LOW_VOLATILITY_PERCENTAGE_THRESHOLD = 0.04; // 0.04%
  private static final double HIGH_VOLATILITY_PERCENTAGE_THRESHOLD = 0.25; // 0.25%

  private final ATRIndicator atrIndicator;
  private final Logger logger = LogManager.getLogger(ATRBlocker.class);

  /**
   * Constructs an ATRBlocker with the specified ATR period.
   *
   * @param atrPeriod the period to use in the ATR calculation
   */
  public ATRBlocker(int atrPeriod) {
    this.atrIndicator = new ATRIndicator(atrPeriod);
    logger.info("ATRBlocker initialized with ATR period: {}", atrPeriod);
  }

  @Override
  public boolean isMomentumToBlocking(List<Bar> bars) {
    if (bars == null || bars.isEmpty()) {
      logger.warn("Bars list is null or empty");
      return false;
    }

    var length = bars.size();
    var highs = new double[length];
    var lows = new double[length];
    var closes = new double[length];

    for (int i = 0; i < length; i++) {
      var bar = bars.get(i);
      highs[i] = bar.high().doubleValue();
      lows[i] = bar.low().doubleValue();
      closes[i] = bar.close().doubleValue();
    }

    List<Double> atrValues;
    try {
      atrValues = atrIndicator.calculate(highs, lows, closes);
    } catch (InsufficientDataException e) {
      logger.warn("Insufficient data to calculate ATR: {}", e.getMessage());
      return false;
    }

    var latestAtr = atrValues.getLast();

    if (latestAtr == null) {
      logger.warn("Could not compute ATR, insufficient data");
      return false;
    }

    var latestClose = closes[closes.length - 1];
    var atrPercentage = (latestAtr / latestClose) * 100;

    if (atrPercentage <= LOW_VOLATILITY_PERCENTAGE_THRESHOLD) {
      logger.info("Low volatility detected (ATR%: {}). Blocking trading.", atrPercentage);
      return true;
    } else if (atrPercentage >= HIGH_VOLATILITY_PERCENTAGE_THRESHOLD) {
      logger.info("High volatility detected (ATR%: {}). Blocking trading.", atrPercentage);
      return true;
    } else {
      logger.info("Normal volatility (ATR%: {}). Not blocking trading.", atrPercentage);
      return false;
    }
  }
}
