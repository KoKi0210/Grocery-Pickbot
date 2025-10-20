package com.example.grocerypickbot.order.controllers;

import com.example.grocerypickbot.order.dtos.OrderDto;
import com.example.grocerypickbot.order.dtos.OrderItemDto;
import com.example.grocerypickbot.order.dtos.OrderItemRequestDto;
import com.example.grocerypickbot.order.dtos.OrderRequestDto;
import com.example.grocerypickbot.order.dtos.OrderResponseDto;
import com.example.grocerypickbot.order.models.OrderStatus;
import com.example.grocerypickbot.order.services.OrderServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class OrderControllerTest {

  @Mock
  private OrderServiceImpl orderService;

  @InjectMocks
  private OrderController orderController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
    objectMapper = new ObjectMapper();
  }

  @Test
  void createOrder_whenValidRequest_shouldReturnCreatedStatus() throws Exception {
    OrderItemRequestDto item1 = new OrderItemRequestDto(1L, 2);
    OrderItemRequestDto item2 = new OrderItemRequestDto(2L, 3);
    OrderRequestDto orderRequest = new OrderRequestDto(Arrays.asList(item1, item2));

    OrderResponseDto orderResponse = OrderResponseDto.success(1L, "Order created successfully");

    when(orderService.createOrder(any(OrderRequestDto.class))).thenReturn(orderResponse);

    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(OrderStatus.SUCCESS.name()))
        .andExpect(jsonPath("$.orderId").value(orderResponse.orderId()))
        .andExpect(jsonPath("$.message").value(orderResponse.message()));

    verify(orderService).createOrder(any(OrderRequestDto.class));
  }

  @Test
  void createOrder_whenItemsListIsEmpty_shouldReturnBadRequest() throws Exception {
    OrderRequestDto orderRequest = new OrderRequestDto(Collections.emptyList());

    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createOrder_whenQuantityIsZero_shouldReturnBadRequest() throws Exception {
    OrderItemRequestDto item = new OrderItemRequestDto(1L, 0);
    OrderRequestDto orderRequest = new OrderRequestDto(List.of(item));

    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createOrder_whenQuantityIsNegative_shouldReturnBadRequest() throws Exception {
    OrderItemRequestDto item = new OrderItemRequestDto(1L, -5);
    OrderRequestDto orderRequest = new OrderRequestDto(List.of(item));

    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createOrder_whenProductIdIsNull_shouldReturnBadRequest() throws Exception {
    OrderItemRequestDto item = new OrderItemRequestDto(null, 5);
    OrderRequestDto orderRequest = new OrderRequestDto(List.of(item));

    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createOrder_whenQuantityIsNull_shouldReturnBadRequest() throws Exception {
    OrderItemRequestDto item = new OrderItemRequestDto(1L, null);
    OrderRequestDto orderRequest = new OrderRequestDto(List.of(item));

    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void createOrder_whenSingleItem_shouldReturnCreatedStatus() throws Exception {
    OrderItemRequestDto item = new OrderItemRequestDto(1L, 5);
    OrderRequestDto orderRequest = new OrderRequestDto(List.of(item));

    OrderResponseDto orderResponse = OrderResponseDto.success(10L, "Order created successfully");

    when(orderService.createOrder(any(OrderRequestDto.class))).thenReturn(orderResponse);

    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(OrderStatus.SUCCESS.name()))
        .andExpect(jsonPath("$.orderId").value(orderResponse.orderId()));

    verify(orderService).createOrder(any(OrderRequestDto.class));
  }

  @Test
  void getOrderById_whenOrderExists_shouldReturnOkWithOrderDetails() throws Exception {
    Long orderId = 1L;
    OrderItemDto orderItem1 = new OrderItemDto(1L, "Apple", 2, new BigDecimal("1.99"), new BigDecimal("3.98"));
    OrderItemDto orderItem2 = new OrderItemDto(2L, "Banana", 3, new BigDecimal("0.99"), new BigDecimal("2.97"));
    OrderDto orderDto = new OrderDto(
        orderId,
        OrderStatus.SUCCESS,
        new BigDecimal("6.95"),
        Arrays.asList(orderItem1, orderItem2)
    );

    when(orderService.getOrderById(orderId)).thenReturn(orderDto);

    mockMvc.perform(get("/orders/{id}", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId").value(orderDto.orderId()))
        .andExpect(jsonPath("$.status").value(String.valueOf(orderDto.status())))
        .andExpect(jsonPath("$.totalPrice").value(orderDto.totalPrice()))
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items[0].productName").value(orderDto.items().get(0).productName()))
        .andExpect(jsonPath("$.items[1].productName").value(orderDto.items().get(1).productName()));

    verify(orderService).getOrderById(orderId);
  }

  @Test
  void getOrderById_whenDifferentOrderId_shouldReturnOkWithCorrectOrder() throws Exception {
    Long orderId = 99L;
    OrderItemDto orderItem = new OrderItemDto(5L, "Orange", 10, new BigDecimal("3.50"), new BigDecimal("35.00"));
    OrderDto orderDto = new OrderDto(
        orderId,
        OrderStatus.SUCCESS,
        new BigDecimal("35.00"),
        List.of(orderItem)
    );

    when(orderService.getOrderById(orderId)).thenReturn(orderDto);

    mockMvc.perform(get("/orders/{id}", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId").value(orderDto.orderId()))
        .andExpect(jsonPath("$.totalPrice").value(orderDto.totalPrice().doubleValue()));

    verify(orderService).getOrderById(orderId);
  }

  @Test
  void getOrderById_whenOrderHasEmptyItems_shouldReturnOkWithEmptyItemsList() throws Exception {
    Long orderId = 5L;
    OrderDto orderDto = new OrderDto(
        orderId,
        OrderStatus.SUCCESS,
        new BigDecimal("0.00"),
        Collections.emptyList()
    );

    when(orderService.getOrderById(orderId)).thenReturn(orderDto);

    mockMvc.perform(get("/orders/{id}", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId").value(orderDto.orderId()))
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items").isEmpty());

    verify(orderService).getOrderById(orderId);
  }

  @Test
  void createOrder_whenLargeQuantity_shouldReturnCreatedStatus() throws Exception {
    OrderItemRequestDto item = new OrderItemRequestDto(1L, 10000000);
    OrderRequestDto orderRequest = new OrderRequestDto(List.of(item));

    OrderResponseDto orderResponse = OrderResponseDto.success(30L, "Order created successfully");

    when(orderService.createOrder(any(OrderRequestDto.class))).thenReturn(orderResponse);

    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.status").value(OrderStatus.SUCCESS.name()));

    verify(orderService).createOrder(any(OrderRequestDto.class));
  }

  @Test
  void createOrder_whenMinimumValidQuantity_shouldReturnCreatedStatus() throws Exception {
    OrderItemRequestDto item = new OrderItemRequestDto(1L, 1);
    OrderRequestDto orderRequest = new OrderRequestDto(List.of(item));

    OrderResponseDto orderResponse = OrderResponseDto.success(40L, "Order created successfully");

    when(orderService.createOrder(any(OrderRequestDto.class))).thenReturn(orderResponse);

    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(orderRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.orderId").value(orderResponse.orderId()));

    verify(orderService).createOrder(any(OrderRequestDto.class));
  }

  @Test
  void getOrderById_whenOrderHasFailStatus_shouldReturnOkWithFailStatus() throws Exception {
    Long orderId = 7L;
    OrderItemDto orderItem = new OrderItemDto(1L, "Product", 5, new BigDecimal("10.00"), new BigDecimal("50.00"));
    OrderDto orderDto = new OrderDto(
        orderId,
        OrderStatus.FAIL,
        new BigDecimal("50.00"),
        List.of(orderItem)
    );

    when(orderService.getOrderById(orderId)).thenReturn(orderDto);

    mockMvc.perform(get("/orders/{id}", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.orderId").value(orderDto.orderId()))
        .andExpect(jsonPath("$.status").value(OrderStatus.FAIL.name()));

    verify(orderService).getOrderById(orderId);
  }

  @Test
  void createOrder_whenItemsIsNull_shouldReturnBadRequest() throws Exception {
    String requestBody = "{}";

    mockMvc.perform(post("/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getOrderById_whenOrderHasMultipleItems_shouldReturnOkWithAllItems() throws Exception {
    Long orderId = 15L;
    OrderItemDto item1 = new OrderItemDto(1L, "Item1", 1, new BigDecimal("5.00"), new BigDecimal("5.00"));
    OrderItemDto item2 = new OrderItemDto(2L, "Item2", 2, new BigDecimal("10.00"), new BigDecimal("20.00"));
    OrderItemDto item3 = new OrderItemDto(3L, "Item3", 3, new BigDecimal("15.00"), new BigDecimal("45.00"));
    OrderDto orderDto = new OrderDto(
        orderId,
        OrderStatus.SUCCESS,
        new BigDecimal("70.00"),
        Arrays.asList(item1, item2, item3)
    );

    when(orderService.getOrderById(orderId)).thenReturn(orderDto);

    mockMvc.perform(get("/orders/{id}", orderId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items.length()").value(orderDto.items().size()));

    verify(orderService).getOrderById(orderId);
  }
}

