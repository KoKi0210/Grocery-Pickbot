package com.example.grocerypickbot.exceptions;

/**
 * Exception thrown when there is insufficient stock for a requested product.
 */
public class InsufficientStockException extends RuntimeException {
  /**
   * Constructs a new InsufficientStockException with a detailed message.
   *
   * @param productName the name of the product
   * @param requested   the quantity requested
   * @param available   the quantity available in stock
   */
  public InsufficientStockException(String productName, int requested, int available) {
    super(String.format(
        "Not enough availability for %s: requested %d, available %d",
        productName, requested, available
    ));
  }
}
