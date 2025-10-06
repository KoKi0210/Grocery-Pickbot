package com.example.grocerypickbot.route.controllers;

import com.example.grocerypickbot.route.models.RouteResponse;
import com.example.grocerypickbot.route.services.RouteService;
import com.example.grocerypickbot.route.services.RouteServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling route-related endpoints.
 *
 * <p>Provides an endpoint to retrieve the optimal route for a given order ID.
 * Delegates route calculation logic to the {@link RouteService}.
 * </p>
 */
@RestController
@CrossOrigin(origins = "http://localhost:5500")
@RequestMapping("/routes")
public class RouteController {
  private final RouteService routeService;

  /**
   *  Constructor for RouteController.
   */
  public RouteController(RouteServiceImpl routeService) {
    this.routeService = routeService;
  }

  /**
   *  Retrieve the route for a given order ID.
   */
  @GetMapping
  public ResponseEntity<RouteResponse> getRoute(@RequestParam Long orderId) {
    return ResponseEntity.ok(routeService.getRoute(orderId));
  }
}
