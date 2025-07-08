package com.example.groceryPickbot.order.controllers;

import com.example.groceryPickbot.order.dtos.OrderDTO;
import com.example.groceryPickbot.order.dtos.OrderRequestDTO;
import com.example.groceryPickbot.order.dtos.OrderResponseDTO;
import com.example.groceryPickbot.order.services.OrderService;
import com.example.groceryPickbot.order.services.OrderServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5500")
@RequestMapping("/orders")
@Validated
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderServiceImpl orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponseDTO> createOrder (@Valid @RequestBody OrderRequestDTO orderRequestDTO){
        return new ResponseEntity<>(orderService.createOrder(orderRequestDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id){
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
