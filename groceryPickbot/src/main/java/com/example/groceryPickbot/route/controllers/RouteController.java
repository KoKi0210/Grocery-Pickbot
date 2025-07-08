package com.example.groceryPickbot.route.controllers;


import com.example.groceryPickbot.route.models.RouteResponse;
import com.example.groceryPickbot.route.services.RouteService;
import com.example.groceryPickbot.route.services.RouteServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5500")
@RequestMapping("/routes")
public class RouteController {
    private RouteService routeService;

    public RouteController(RouteServiceImpl routeService) {
        this.routeService = routeService;
    }


    @GetMapping
    public ResponseEntity<RouteResponse> getRoute(@RequestParam Long orderId){
        return ResponseEntity.ok(routeService.getRoute(orderId));
    }
}
