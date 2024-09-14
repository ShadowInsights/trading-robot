package org.shadow.infrastructure.client.fake.exception;

public class FailedToInitException extends RuntimeException {

  public FailedToInitException() {
    super("Failed to init client");
  }
}
