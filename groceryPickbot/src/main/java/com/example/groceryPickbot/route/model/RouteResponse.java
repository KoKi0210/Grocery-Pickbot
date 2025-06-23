package com.example.groceryPickbot.route.model;

import com.example.groceryPickbot.order.model.OrderStatus;

import java.util.List;

public class RouteResponse {
    private Long orderId;
    private OrderStatus status;
    private List<int[]> visitedLocations;

    public RouteResponse(Long orderId, OrderStatus status, List<int[]> visitedLocations) {
        this.orderId = orderId;
        this.status = status;
        this.visitedLocations = visitedLocations;
    }

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

    public List<int[]> getVisitedLocations() {
        return visitedLocations;
    }

    public void setVisitedLocations(List<int[]> visitedLocations) {
        this.visitedLocations = visitedLocations;
    }
}
