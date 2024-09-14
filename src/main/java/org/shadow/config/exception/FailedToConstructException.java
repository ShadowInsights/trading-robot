package org.shadow.config.exception;

public class FailedToConstructException extends RuntimeException {

  public FailedToConstructException(Class<?> clazz) {
    super("Failed to construct class: " + clazz.getName());
  }

  public FailedToConstructException(Class<?> clazz, Throwable cause) {
    super("Failed to construct class: " + clazz.getName(), cause);
  }
}
