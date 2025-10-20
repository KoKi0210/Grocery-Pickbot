package com.example.grocerypickbot.order.services;

import com.example.grocerypickbot.exceptions.OrderNotFoundException;
import com.example.grocerypickbot.exceptions.ProductNotFoundException;
import com.example.grocerypickbot.order.dtos.MissingItemDto;
import com.example.grocerypickbot.order.dtos.OrderDto;
import com.example.grocerypickbot.order.dtos.OrderItemRequestDto;
import com.example.grocerypickbot.order.dtos.OrderRequestDto;
import com.example.grocerypickbot.order.dtos.OrderResponseDto;
import com.example.grocerypickbot.order.mappers.OrderMapper;
import com.example.grocerypickbot.order.models.Order;
import com.example.grocerypickbot.order.models.OrderItem;
import com.example.grocerypickbot.order.models.OrderStatus;
import com.example.grocerypickbot.order.repositories.OrderRepository;
import com.example.grocerypickbot.product.models.Product;
import com.example.grocerypickbot.product.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createOrder_whenValidRequestWithSufficientStock_shouldReturnSuccessResponse() {
        OrderItemRequestDto orderItemDto = new OrderItemRequestDto(1L, 2);
        List<OrderItemRequestDto> items = List.of(orderItemDto);
        OrderRequestDto requestDto = new OrderRequestDto(items);

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Test Product");
        mockProduct.setQuantity(10);

        Order mockOrder = new Order();
        mockOrder.setOrderItems(new ArrayList<>());

        Order savedOrder = new Order();
        savedOrder.setId(1L);

        OrderItem mockOrderItem = new OrderItem();

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
        when(orderMapper.toOrderEntity(requestDto)).thenReturn(mockOrder);
        when(orderMapper.toOrderItemEntity(orderItemDto)).thenReturn(mockOrderItem);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponseDto response = orderService.createOrder(requestDto);

        assertEquals(OrderStatus.SUCCESS, response.status());
        assertEquals(1L, response.orderId());
        assertEquals("Order ready! Please collect it at the desk", response.message());

        assertEquals(8, mockProduct.getQuantity());
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void createOrder_whenInsufficientStock_shouldReturnFailureResponseWithMissingItems() {
        OrderItemRequestDto orderItemDto = new OrderItemRequestDto(1L, 15);
        List<OrderItemRequestDto> items = List.of(orderItemDto);
        OrderRequestDto requestDto = new OrderRequestDto(items);

        Product mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Test Product");
        mockProduct.setQuantity(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));

        OrderResponseDto response = orderService.createOrder(requestDto);

        assertNotEquals(OrderStatus.SUCCESS, response.status());
        assertEquals("Insufficient availability", response.message());
        assertNotNull(response.missingItems());
        assertEquals(1, response.missingItems().size());

        MissingItemDto missingItem = response.missingItems().get(0);
        assertEquals("Test Product", missingItem.productName());
        assertEquals(15, missingItem.requested());
        assertEquals(10, missingItem.available());

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void createOrder_whenProductNotFound_shouldThrowProductNotFoundException() {
        OrderItemRequestDto orderItemDto = new OrderItemRequestDto(999L, 2);
        List<OrderItemRequestDto> items = List.of(orderItemDto);
        OrderRequestDto requestDto = new OrderRequestDto(items);

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
            () -> orderService.createOrder(requestDto));
    }

    @Test
    void getAllOrders_whenOrdersExist_shouldReturnAllOrderDtos() {
        Order order1 = new Order();
        order1.setId(1L);
        Order order2 = new Order();
        order2.setId(2L);

        List<Order> orders = Arrays.asList(order1, order2);

        OrderDto orderDto1 = new OrderDto(1L, OrderStatus.SUCCESS, BigDecimal.valueOf(10.2),new ArrayList<>());
        OrderDto orderDto2 = new OrderDto(2L, OrderStatus.SUCCESS, BigDecimal.valueOf(4.65),new ArrayList<>());

        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toOrderDto(order1)).thenReturn(orderDto1);
        when(orderMapper.toOrderDto(order2)).thenReturn(orderDto2);

        List<OrderDto> result = orderService.getAllOrders();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).orderId());
        assertEquals(2L, result.get(1).orderId());
        verify(orderRepository).findAll();
    }

    @Test
    void getOrderById_whenValidOrderId_shouldReturnOrderDto() {
        Long orderId = 1L;
        Order order = new Order();
        order.setId(orderId);

        OrderDto expectedDto = new OrderDto(orderId, OrderStatus.SUCCESS, BigDecimal.ONE,new ArrayList<>());

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderMapper.toOrderDto(order)).thenReturn(expectedDto);

        OrderDto result = orderService.getOrderById(orderId);

        assertNotNull(result);
        assertEquals(orderId, result.orderId());
        verify(orderRepository).findById(orderId);
        verify(orderMapper).toOrderDto(order);
    }

    @Test
    void getOrderById_whenOrderNotFound_shouldThrowOrderNotFoundException() {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class,
            () -> orderService.getOrderById(orderId));

        verify(orderRepository).findById(orderId);
    }

    @Test
    void createOrder_whenMultipleItemsWithPartialStock_shouldReturnFailureResponseWithMissingItems() {
        OrderItemRequestDto item1 = new OrderItemRequestDto(1L, 5);
        OrderItemRequestDto item2 = new OrderItemRequestDto(2L, 20);
        List<OrderItemRequestDto> items = List.of(item1, item2);
        OrderRequestDto requestDto = new OrderRequestDto(items);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setQuantity(15);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));

        OrderResponseDto response = orderService.createOrder(requestDto);

        assertNotEquals(OrderStatus.SUCCESS, response.status());
        assertEquals("Insufficient availability", response.message());
        assertEquals(1, response.missingItems().size());

        MissingItemDto missingItem = response.missingItems().get(0);
        assertEquals("Product 2", missingItem.productName());
        assertEquals(20, missingItem.requested());
        assertEquals(15, missingItem.available());
    }

    @Test
    void createOrder_whenMultipleItemsWithSufficientStock_shouldReturnSuccessResponseAndUpdateStock() {
        OrderItemRequestDto item1 = new OrderItemRequestDto(1L, 3);
        OrderItemRequestDto item2 = new OrderItemRequestDto(2L, 5);
        List<OrderItemRequestDto> items = List.of(item1, item2);
        OrderRequestDto requestDto = new OrderRequestDto(items);

        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");
        product1.setQuantity(10);

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");
        product2.setQuantity(15);

        Order mockOrder = new Order();
        mockOrder.setOrderItems(new ArrayList<>());

        Order savedOrder = new Order();
        savedOrder.setId(1L);

        OrderItem mockOrderItem1 = new OrderItem();
        OrderItem mockOrderItem2 = new OrderItem();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(orderMapper.toOrderEntity(requestDto)).thenReturn(mockOrder);
        when(orderMapper.toOrderItemEntity(item1)).thenReturn(mockOrderItem1);
        when(orderMapper.toOrderItemEntity(item2)).thenReturn(mockOrderItem2);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponseDto response = orderService.createOrder(requestDto);

        assertEquals(OrderStatus.SUCCESS, response.status());
        assertEquals(1L, response.orderId());

        assertEquals(7, product1.getQuantity());
        assertEquals(10, product2.getQuantity());
    }
}
