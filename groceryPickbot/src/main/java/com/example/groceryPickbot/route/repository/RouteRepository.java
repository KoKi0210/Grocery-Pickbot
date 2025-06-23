package com.example.groceryPickbot.route.repository;

import com.example.groceryPickbot.route.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findByOrderId(Long orderId);
}
