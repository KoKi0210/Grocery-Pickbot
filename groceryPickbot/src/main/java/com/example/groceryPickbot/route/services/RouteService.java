package com.example.groceryPickbot.route.services;

import com.example.groceryPickbot.route.model.RouteResponse;

import java.util.List;

public interface RouteService {
    RouteResponse getRoute(Long id);

}
