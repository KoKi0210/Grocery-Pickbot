package com.example.grocerypickbot.route.services;

import com.example.grocerypickbot.route.models.RouteResponse;
import java.util.List;

/**
 * Service interface for handling route-related operations.
 */
public interface RouteService {
  /**
   * Retrieves the route information for a given order ID.
   *
   * @param id the ID of the order
   * @return the route response containing order details and visited locations
   */
  List<RouteResponse> getRoute(Long id);

}
