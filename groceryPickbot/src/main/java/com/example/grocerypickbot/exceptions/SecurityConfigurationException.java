package com.example.grocerypickbot.exceptions;

/**
 * Exception thrown when there is a security configuration error.
 */
public class SecurityConfigurationException extends Exception {
  /**
   * Constructs a new SecurityConfigurationException with a detailed message and cause.
   *
   * @param message the detail message
   * @param cause   the cause of the exception
   */
  public SecurityConfigurationException(String message, Throwable cause) {
    super(message);
  }
}
