package org.shadow.config.validator;

import java.util.List;
import org.shadow.config.Config;
import org.shadow.config.model.ExchangeConfigurationType;

public class HistoricalDataFileValidator implements Validator<Config> {

  @Override
  public List<String> validate(Config config) {
    if (!config.exchangeConfiguration().type().equals(ExchangeConfigurationType.FAKE)) {
      return config.robotConfigurations().stream()
          .filter(robotConfig -> robotConfig.historicalDataFile().isPresent())
          .map(
              robotConfig ->
                  String.format(
                      "Robot of type %s can not contain historicalDataFile.", robotConfig.type()))
          .toList();
    }

    return List.of();
  }
}
