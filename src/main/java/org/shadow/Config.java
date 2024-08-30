package org.shadow;

import com.typesafe.config.ConfigFactory;

public record Config(String apiKey, String apiSecret) {
  public static Config load() {
    var config = ConfigFactory.load();
    return new Config(config.getString("trading.apiKey"), config.getString("trading.apiSecret"));
  }
}
