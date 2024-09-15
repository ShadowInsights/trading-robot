package org.shadow.config.validator;

import java.util.ArrayList;
import java.util.List;
import org.shadow.config.Config;

public class ConfigValidator {

  private final List<Validator<Config>> validators = new ArrayList<>();

  public void addValidator(Validator<Config> validator) {
    validators.add(validator);
  }

  public List<String> validate(Config config) {
    List<String> errors = new ArrayList<>();

    for (Validator<Config> validator : validators) {
      errors.addAll(validator.validate(config));
    }

    return errors;
  }
}
