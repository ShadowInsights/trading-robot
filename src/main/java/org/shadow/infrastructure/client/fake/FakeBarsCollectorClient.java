package org.shadow.infrastructure.client.fake;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.shadow.domain.client.BarsCollectorClient;
import org.shadow.domain.client.model.Bar;
import org.shadow.infrastructure.client.fake.exception.FailedToInitException;
import org.shadow.infrastructure.file.HistoricalDataLoader;

public class FakeBarsCollectorClient implements BarsCollectorClient {

  private final Logger logger = LogManager.getLogger(FakeBarsCollectorClient.class);
  private final HistoricalDataLoader historicalDataLoader;
  private final String historicalDataFile;

  public FakeBarsCollectorClient(
      HistoricalDataLoader historicalDataLoader, String historicalDataFile) {
    this.historicalDataLoader = historicalDataLoader;
    this.historicalDataFile = historicalDataFile;
  }

  @Override
  public synchronized void init() {
    logger.info("Initializing FakeBarsCollectorClient...");
    // TODO: Implement data loading and handling: TR-3
    try {
      var candlesticks = historicalDataLoader.load(historicalDataFile);
      logger.info("Loaded {} candlesticks from {} file", candlesticks.size(), historicalDataFile);
    } catch (IOException ioException) {
      logger.error(ioException);
      throw new FailedToInitException();
    }
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
