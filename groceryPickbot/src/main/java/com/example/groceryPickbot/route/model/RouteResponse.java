package com.example.groceryPickbot.route.model;

import com.example.groceryPickbot.order.model.OrderStatus;

import java.util.List;

public record RouteResponse(
        Long orderId,
        OrderStatus status,
        List<int[]> visitedLocations
) {}
