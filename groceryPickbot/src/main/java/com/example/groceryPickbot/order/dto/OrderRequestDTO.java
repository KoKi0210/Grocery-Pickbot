package com.example.groceryPickbot.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderRequestDTO(
        @Valid
        @NotEmpty(message = "Order must contain at least one item")
        List<OrderItemRequestDTO> items
) {}
