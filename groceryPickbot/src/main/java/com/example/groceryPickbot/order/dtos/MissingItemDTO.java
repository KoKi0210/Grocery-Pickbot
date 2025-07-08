package com.example.groceryPickbot.order.dtos;

public record MissingItemDTO(
        String productName,
        int requested,
        int available
        ) {}
