package com.example.grocerypickbot.exceptions;

import java.util.Map;

/**
 * Exception thrown when user registration fails due to invalid input.
 */
public class InvalidUserRegistrationException extends RuntimeException {
  private final Map<String, String> errors;

  /**
   * Constructs a new InvalidUserRegistrationException with a map of error messages.
   *
   * @param errors a map where keys are field names and values are error messages
   */
  public InvalidUserRegistrationException(Map<String, String> errors) {
    super("Invalid user registration");
    this.errors = errors;
  }

  public Map<String, String> getErrors() {
    return errors;
  }
}

