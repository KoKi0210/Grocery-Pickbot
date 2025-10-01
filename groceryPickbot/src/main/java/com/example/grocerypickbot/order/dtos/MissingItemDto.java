package com.example.grocerypickbot.order.dtos;

/**
 * Data Transfer Object representing a missing item in an order.
 *
 * @param productName the name of the product
 * @param requested   the quantity of the product requested
 * @param available   the quantity of the product available
 */
public record MissingItemDto(
    String productName,
    int requested,
    int available
) {
}
