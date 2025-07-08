package com.example.groceryPickbot.route.models;

import com.example.groceryPickbot.order.models.OrderStatus;

import java.util.List;

public record RouteResponse(
        Long orderId,
        OrderStatus status,
        List<int[]> visitedLocations
) {}
