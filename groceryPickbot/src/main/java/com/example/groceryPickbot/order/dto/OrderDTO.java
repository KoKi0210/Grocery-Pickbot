package com.example.groceryPickbot.order.dto;

import com.example.groceryPickbot.order.model.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public class OrderDTO {
    private Long orderId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private List<OrderItemDTO> items;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }
}
