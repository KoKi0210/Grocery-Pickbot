package com.example.groceryPickbot.route.services;

import com.example.groceryPickbot.route.models.RouteResponse;

public interface RouteService {
    RouteResponse getRoute(Long id);

}
