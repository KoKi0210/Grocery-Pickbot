package com.example.groceryPickbot.order.services;

import com.example.groceryPickbot.order.dto.OrderDTO;
import com.example.groceryPickbot.order.dto.OrderRequestDTO;
import com.example.groceryPickbot.order.dto.OrderResponseDTO;

import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(OrderRequestDTO orderDTO);
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(Long id);
}
