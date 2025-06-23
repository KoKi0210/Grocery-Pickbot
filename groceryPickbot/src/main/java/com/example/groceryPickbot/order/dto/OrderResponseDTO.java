package com.example.groceryPickbot.order.dto;

import com.example.groceryPickbot.order.model.OrderStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OrderResponseDTO {
    private OrderStatus status;
    private Long orderId;
    private String message;
    private List<MissingItemDTO> missingItems;

    public OrderResponseDTO(OrderStatus status, Long orderId, String message, List<MissingItemDTO> missingItems) {
        this.status = status;
        this.orderId = orderId;
        this.message = message;
        this.missingItems = missingItems;
    }

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

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<MissingItemDTO> getMissingItems() {
        return missingItems;
    }

    public void setMissingItems(List<MissingItemDTO> missingItems) {
        this.missingItems = missingItems;
    }
}
