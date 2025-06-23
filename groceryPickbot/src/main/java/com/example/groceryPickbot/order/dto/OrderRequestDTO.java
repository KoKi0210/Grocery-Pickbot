package com.example.groceryPickbot.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class OrderRequestDTO {
    @Valid
    @NotEmpty(message = "Order must contain at least one item")
    private List<OrderItemRequestDTO> items;

    public @Valid @NotEmpty List<OrderItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(@Valid @NotEmpty List<OrderItemRequestDTO> items) {
        this.items = items;
    }
}
