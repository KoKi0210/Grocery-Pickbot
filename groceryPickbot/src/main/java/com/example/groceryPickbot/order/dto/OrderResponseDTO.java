package com.example.groceryPickbot.order.dto;

import com.example.groceryPickbot.order.model.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record OrderResponseDTO(
        OrderStatus status,
        Long orderId,
        String message,
        List<MissingItemDTO> missingItems
) {
    public static OrderResponseDTO success(Long orderId, String message) {
        return new OrderResponseDTO(
                OrderStatus.SUCCESS,
                orderId,
                message,
                Collections.emptyList()
        );
    }

    public static OrderResponseDTO failed(String message, List<MissingItemDTO> missingItems) {
        return new OrderResponseDTO(
                OrderStatus.FAIL,
                null,
                message,
                missingItems
        );
    }
}

