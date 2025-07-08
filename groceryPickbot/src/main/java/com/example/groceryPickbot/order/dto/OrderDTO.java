package com.example.groceryPickbot.order.dto;

import com.example.groceryPickbot.order.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderDTO(
         Long orderId,
         OrderStatus status,
         BigDecimal totalPrice,
         List<OrderItemDTO> items
) {}
