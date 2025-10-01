package com.example.grocerypickbot.order.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * DTO representing an order request containing a list of order items.
 *
 * @param items List of items in the order, must not be empty.
 */
public record OrderRequestDto(
    @Valid
    @NotEmpty(message = "Order must contain at least one item")
    List<OrderItemRequestDto> items
) {
}
