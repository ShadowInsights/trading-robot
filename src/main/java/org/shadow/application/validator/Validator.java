package org.shadow.application.validator;

public interface Validator<T> {

  boolean isValid(T context);
}
