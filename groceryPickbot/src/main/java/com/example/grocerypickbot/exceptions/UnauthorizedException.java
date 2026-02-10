package com.example.grocerypickbot.exceptions;

import java.util.Map;

/**
 * Exception thrown when a user is not authenticated.
 */
public class UnauthorizedException extends RuntimeException {
  private final Map<String, String> errors;
  /**
   * Constructs a new UnauthorizedException with a map of error messages.
   *
   * @param errors a map where keys are field names and values are error messages
   */
  public UnauthorizedException(Map<String, String> errors) {
    super("Unauthorized access");
    this.errors = errors;
  }

  public Map<String, String> getErrors() {
    return errors;
  }}

