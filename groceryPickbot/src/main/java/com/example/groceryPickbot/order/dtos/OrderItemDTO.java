package com.example.groceryPickbot.order.dtos;

import java.math.BigDecimal;

public record OrderItemDTO(
         Long productId,
         String productName,
         Integer quantity,
         BigDecimal unitPrice,
         BigDecimal itemTotal
) { }
