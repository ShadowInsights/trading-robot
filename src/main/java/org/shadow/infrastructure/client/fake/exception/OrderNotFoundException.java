package org.shadow.infrastructure.client.fake.exception;

/** Exception thrown when an order is not found. */
public class OrderNotFoundException extends RuntimeException {
  public OrderNotFoundException(String message) {
    super(message);
  }
}
