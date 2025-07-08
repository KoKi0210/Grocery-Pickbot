package com.example.groceryPickbot.order.repositories;

import com.example.groceryPickbot.order.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
