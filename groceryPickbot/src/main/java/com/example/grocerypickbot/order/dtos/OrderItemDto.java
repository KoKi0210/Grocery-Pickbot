package com.example.grocerypickbot.order.dtos;

import java.math.BigDecimal;

/**
 * Data Transfer Object representing an item in an order.
 *
 * @param productId   the ID of the product
 * @param productName the name of the product
 * @param quantity    the quantity of the product ordered
 * @param unitPrice   the price per unit of the product
 * @param itemTotal   the total price for this item (unitPrice * quantity)
 */
public record OrderItemDto(
    Long productId,
    String productName,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal itemTotal
) {
}
