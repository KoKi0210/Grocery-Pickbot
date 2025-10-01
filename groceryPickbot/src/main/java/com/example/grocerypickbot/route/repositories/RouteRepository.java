package com.example.grocerypickbot.route.repositories;

import com.example.grocerypickbot.route.models.Route;
import java.util.Optional;
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
  Optional<Route> findByOrderId(Long orderId);
}
