package com.example.grocerypickbot.order.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for an item in an order request.
 *
 * @param productId The ID of the product being ordered.
 * @param quantity  The quantity of the product being ordered.
 */
public record OrderItemRequestDto(
    @NotNull(message = "Product ID is required")
    Long productId,

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity
) {
}
