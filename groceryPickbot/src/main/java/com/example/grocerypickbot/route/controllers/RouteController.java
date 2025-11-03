package com.example.grocerypickbot.route.controllers;

import com.example.grocerypickbot.route.models.RouteResponse;
import com.example.grocerypickbot.route.services.RouteService;
import com.example.grocerypickbot.route.services.RouteServiceImpl;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/routes")
public class RouteController {
  private static final Logger LOGGER = LoggerFactory.getLogger(RouteController.class);
  private final RouteServiceImpl routeService;

  /**
   * Constructor for RouteController.
   */
  public RouteController(RouteServiceImpl routeService) {
    this.routeService = routeService;
  }

  /**
   * Retrieve the route for a given order ID.
   */
  @GetMapping
  public ResponseEntity<List<RouteResponse>> getRoute(@RequestParam Long orderId,
                                                      @RequestParam boolean collectInParallel) {
    long start = System.currentTimeMillis();
    if (collectInParallel) {
      routeService.collectItemsParallel(orderId);
    } else {
      routeService.calculateAndSavePath(orderId);
    }
    long end = System.currentTimeMillis();
    long durationMs = (end - start);
    if (collectInParallel) {
      LOGGER.info("Parallel collection for order {} took {} ms", orderId, durationMs);
    } else {
      LOGGER.info("Single-threaded collection for order {} took {} ms", orderId, durationMs);
    }
    return ResponseEntity.ok(routeService.getRoute(orderId));
  }
}