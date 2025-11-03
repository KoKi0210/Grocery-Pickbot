package com.example.grocerypickbot.exceptions;

import java.util.Map;

/**
 * Exception thrown when product data is invalid.
 *
 * <p>Contains a map of field names to error messages indicating the validation issues.</p>
 */
public class InvalidProductDataException extends RuntimeException {
  private final Map<String, String> errors;

  /**
   * Constructs a new InvalidProductDataException with the specified errors.
   *
   * @param errors a map of field names to error messages
   */
  public InvalidProductDataException(Map<String, String> errors) {
    super("Invalid product data");
    this.errors = errors;
  }

  public Map<String, String> getErrors() {
    return errors;
  }
}
