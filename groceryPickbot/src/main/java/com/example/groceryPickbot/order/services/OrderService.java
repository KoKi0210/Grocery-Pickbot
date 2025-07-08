package com.example.groceryPickbot.order.services;

import com.example.groceryPickbot.order.dtos.OrderDTO;
import com.example.groceryPickbot.order.dtos.OrderRequestDTO;
import com.example.groceryPickbot.order.dtos.OrderResponseDTO;

import java.util.List;

public interface OrderService {

    OrderResponseDTO createOrder(OrderRequestDTO orderDTO);
    List<OrderDTO> getAllOrders();
    OrderDTO getOrderById(Long id);
}
