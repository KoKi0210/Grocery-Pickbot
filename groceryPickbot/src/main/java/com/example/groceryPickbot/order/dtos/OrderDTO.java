package com.example.groceryPickbot.order.dtos;

import com.example.groceryPickbot.order.models.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderDTO(
         Long orderId,
         OrderStatus status,
         BigDecimal totalPrice,
         List<OrderItemDTO> items
) {}
