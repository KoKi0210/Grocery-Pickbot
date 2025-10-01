package com.example.grocerypickbot.order.repositories;

import com.example.grocerypickbot.order.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing Order entities.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
