package com.example.grocerypickbot.exceptions;

/**
 * Exception thrown when an order with a specified ID is not found.
 */
public class OrderNotFoundException extends RuntimeException {

  /**
   * Constructs a new OrderNotFoundException with a detailed message.
   *
   * @param id the ID of the order that was not found
   */
  public OrderNotFoundException(Long id) {
    super("Order with id: " + id + " not found");
  }
}
