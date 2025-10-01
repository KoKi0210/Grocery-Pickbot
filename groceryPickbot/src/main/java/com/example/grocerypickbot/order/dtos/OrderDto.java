package com.example.grocerypickbot.order.dtos;

import com.example.grocerypickbot.order.models.OrderStatus;
import java.math.BigDecimal;
import java.util.List;

/**
 * Data Transfer Object representing an order.
 *
 * @param orderId    the unique identifier of the order
 * @param status     the current status of the order
 * @param totalPrice the total price of the order
 * @param items      the list of items in the order
 */
public record OrderDto(
    Long orderId,
    OrderStatus status,
    BigDecimal totalPrice,
    List<OrderItemDto> items
) {
}
