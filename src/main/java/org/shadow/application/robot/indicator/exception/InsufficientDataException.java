package org.shadow.application.robot.indicator.exception;

/** Exception thrown when there is insufficient data to perform a calculation. */
public class InsufficientDataException extends RuntimeException {
  /**
   * Constructs a new InsufficientDataException with the specified detail message.
   *
   * @param message The detail message.
   */
  public InsufficientDataException(String message) {
    super(message);
  }
}
