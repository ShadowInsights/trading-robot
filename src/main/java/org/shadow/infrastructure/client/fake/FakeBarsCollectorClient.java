package org.shadow.infrastructure.client.fake;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedList;
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
  private final String symbol;

  private List<Bar> bars;

  public FakeBarsCollectorClient(
      HistoricalDataLoader historicalDataLoader, String historicalDataFile, String symbol) {
    this.historicalDataLoader = historicalDataLoader;
    this.historicalDataFile = historicalDataFile;
    this.symbol = symbol;
  }

  @Override
  public synchronized void init() {
    logger.info("Initializing FakeBarsCollectorClient...");
    // TODO: Implement data loading and handling: TR-3
    try {
      logger.info("Loading historicalDataBars from {} file", historicalDataFile);
      var historicalDataBars = historicalDataLoader.load(historicalDataFile);
      bars =
          historicalDataBars.stream()
              .map(
                  candlestick ->
                      new Bar(
                          Instant.ofEpochMilli(candlestick.timestamp()),
                          new BigDecimal(candlestick.open()),
                          new BigDecimal(candlestick.high()),
                          new BigDecimal(candlestick.low()),
                          new BigDecimal(candlestick.close()),
                          new BigDecimal(candlestick.volume())))
              .collect(LinkedList::new, LinkedList::add, LinkedList::addAll);
      logger.info(
          "Loaded {} historicalDataBars from {} file",
          historicalDataBars.size(),
          historicalDataFile);
    } catch (IOException ioException) {
      logger.error(ioException);
      throw new FailedToInitException();
    }
    logger.info("FakeBarsCollectorClient initialized successfully.");
  }

  @Override
  public List<Bar> collectBars(TimeUnit interval, long range, Instant timeFrom, Instant timeTo) {
    logger.info(
        "Collecting {} bars with parameters - Interval: {}, Range: {}, TimeFrom: {}, TimeTo: {}",
        symbol,
        interval,
        range,
        timeFrom,
        timeTo);

    return List.of(bars.removeFirst());
  }
}
