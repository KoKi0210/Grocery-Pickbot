package com.example.grocerypickbot.route.repositories;

import com.example.grocerypickbot.route.models.Route;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Route entities.
 */
public interface RouteRepository extends JpaRepository<Route, Long> {

  /**
   * Finds a Route by the associated order ID.
   *
   * @param orderId the ID of the order
   * @return an Optional containing the Route if found, or empty if not found
   */
  List<Route> findByOrderId(Long orderId);
}
