package com.example.grocerypickbot.order.services;

import com.example.grocerypickbot.order.dtos.OrderDto;
import com.example.grocerypickbot.order.dtos.OrderRequestDto;
import com.example.grocerypickbot.order.dtos.OrderResponseDto;
import java.util.List;

/**
 * Service interface for handling order-related operations.
 */
public interface OrderService {

  /**
   * Creates a new order based on the provided order data.
   *
   * @param orderDto the data transfer object containing order details
   * @return the response DTO containing information about the created order
   */
  OrderResponseDto createOrder(OrderRequestDto orderDto);

  /**
   * Retrieves all orders.
   *
   * @return a list of OrderDto representing all orders
   */
  List<OrderDto> getAllOrders();

  /**
   * Retrieves an order by its ID.
   *
   * @param id the ID of the order to retrieve
   * @return the OrderDto representing the order with the specified ID
   */
  OrderDto getOrderById(Long id);
}
