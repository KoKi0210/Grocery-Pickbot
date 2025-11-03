package com.example.grocerypickbot.order.controllers;

import com.example.grocerypickbot.order.dtos.OrderDto;
import com.example.grocerypickbot.order.dtos.OrderRequestDto;
import com.example.grocerypickbot.order.dtos.OrderResponseDto;
import com.example.grocerypickbot.order.services.OrderService;
import com.example.grocerypickbot.order.services.OrderServiceImpl;
import com.example.grocerypickbot.security.annotation.RoleAccess;
import com.example.grocerypickbot.user.models.Role;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing order-related endpoints.
 *
 * <p>Provides endpoints to create new orders and retrieve existing orders by ID.
 * Delegates business logic to the {@link OrderService}.
 * </p>
 */
@RestController
@RequestMapping("/orders")
@Validated
@RoleAccess(allowedRoles = {Role.ADMIN, Role.USER})
public class OrderController {

  private final OrderService orderService;

  /**
   * Constructor for OrderController.
   *
   * @param orderService the order service implementation
   */
  public OrderController(OrderServiceImpl orderService) {
    this.orderService = orderService;
  }

  /**
   * Creates a new order.
   *
   * @param orderRequestDto the order request data transfer object
   * @return ResponseEntity containing the created order response and HTTP status
   */
  @PostMapping
  public ResponseEntity<OrderResponseDto> createOrder(
      @Valid @RequestBody OrderRequestDto orderRequestDto) {
    return new ResponseEntity<>(orderService.createOrder(orderRequestDto), HttpStatus.CREATED);
  }

  /**
   * Retrieves an order by its ID.
   *
   * @param id the ID of the order to retrieve
   * @return ResponseEntity containing the order data transfer object
   */
  @GetMapping("/{id}")
  public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
    return ResponseEntity.ok(orderService.getOrderById(id));
  }
}
