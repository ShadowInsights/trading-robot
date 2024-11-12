package org.shadow.infrastructure.client.fake.exception;

/** Exception thrown when there is insufficient balance to perform an operation. */
public class InsufficientBalanceException extends RuntimeException {
  public InsufficientBalanceException(String message) {
    super(message);
  }
}
