package com.example.groceryPickbot.order.dto;

public record MissingItemDTO(
        String productName,
        int requested,
        int available
        ) {}
