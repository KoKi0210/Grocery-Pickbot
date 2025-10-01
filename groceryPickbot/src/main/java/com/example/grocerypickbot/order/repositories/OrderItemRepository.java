package com.example.grocerypickbot.order.repositories;

import com.example.grocerypickbot.order.models.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing OrderItem entities.
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

  /**
   * Finds all OrderItems associated with a specific order ID.
   *
   * @param orderId the ID of the order
   * @return a list of OrderItems associated with the given order ID
   */
  List<OrderItem> findByOrderId(Long orderId);
}
