package com.example.grocerypickbot.route.models;

import com.example.grocerypickbot.order.models.OrderStatus;
import java.util.List;

/**
 * Response model for route information.
 *
 * @param orderId          the ID of the order
 * @param status           the current status of the order
 * @param visitedLocations a list of visited locations represented as arrays of integers
 */
public record RouteResponse(
    Long orderId,
    OrderStatus status,
    List<int[]> visitedLocations
) {
}
