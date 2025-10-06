package com.example.grocerypickbot.exceptions;

/**
 * Exception thrown when a user is not authenticated.
 */
public class UnauthorizedException extends RuntimeException {
  /**
   * Constructs a new UnauthorizedException with the specified detail message.
   *
   * @param message the detail message
   */
  public UnauthorizedException(String message) {
    super(message);
  }
}

