package org.shadow.infrastructure.fake;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.domain.client.BarsCollectorClient;
import org.shadow.domain.client.model.Bar;

public class FakeBarsCollectorClient implements BarsCollectorClient {

  private static final Logger logger = LogManager.getLogger(FakeBarsCollectorClient.class);

  @Override
  public void init() {
    logger.info("Initializing FakeBarsCollectorClient...");
  }

  @Override
  public List<Bar> collectBars(TimeUnit interval, long range, Instant timeFrom, Instant timeTo) {
    logger.info(
        "Collecting bars with parameters - Interval: {}, Range: {}, TimeFrom: {}, TimeTo: {}",
        interval,
        range,
        timeFrom,
        timeTo);

    logger.debug("Returning empty list of bars as this is a fake implementation.");

    return List.of();
  }
}
