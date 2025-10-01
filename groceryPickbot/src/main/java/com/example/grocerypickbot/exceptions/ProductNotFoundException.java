package com.example.grocerypickbot.exceptions;

/**
 * Exception thrown when a product with a specified ID is not found.
 */
public class ProductNotFoundException extends RuntimeException {

  /**
   * Constructs a new ProductNotFoundException with a detailed message.
   *
   * @param id the ID of the product that was not found
   */
  public ProductNotFoundException(Long id) {
    super("Product with id: " + id + " not found");
  }
}
