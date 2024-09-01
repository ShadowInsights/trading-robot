package org.shadow.domain.client;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.shadow.domain.client.model.Bar;

public interface BarsCollectorClient {

  void init();

  List<Bar> collectBars(TimeUnit interval, long range, Instant timeFrom, Instant timeTo);
}
