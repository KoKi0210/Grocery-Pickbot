package com.example.grocerypickbot.order.mappers;

import com.example.grocerypickbot.order.dtos.OrderDto;
import com.example.grocerypickbot.order.dtos.OrderItemDto;
import com.example.grocerypickbot.order.dtos.OrderItemRequestDto;
import com.example.grocerypickbot.order.dtos.OrderRequestDto;
import com.example.grocerypickbot.order.models.Order;
import com.example.grocerypickbot.order.models.OrderItem;
import com.example.grocerypickbot.order.models.OrderStatus;
import com.example.grocerypickbot.product.models.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class OrderMapperTest {

    private final OrderMapper orderMapper = new OrderMapperImpl();

    @Test
    void toOrderDto_shouldCorrectlyMapOrderToOrderDto() {
        Product product1 = createProduct(1L, "Product 1", new BigDecimal("10.50"));
        Product product2 = createProduct(2L, "Product 2", new BigDecimal("5.25"));

        OrderItem item1 = createOrderItem(1L, product1, 2);
        OrderItem item2 = createOrderItem(2L, product2, 3);

        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.SUCCESS);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(List.of(item1, item2));

        OrderDto result = orderMapper.toOrderDto(order);

        assertNotNull(result);
        assertEquals(1L, result.orderId());
        assertEquals(OrderStatus.SUCCESS, result.status());
        assertEquals(new BigDecimal("36.75"), result.totalPrice());
        assertEquals(2, result.items().size());

        OrderItemDto firstItem = result.items().get(0);
        assertEquals(1L, firstItem.productId());
        assertEquals("Product 1", firstItem.productName());
        assertEquals(2, firstItem.quantity());
        assertEquals(new BigDecimal("10.50"), firstItem.unitPrice());
        assertEquals(new BigDecimal("21.00"), firstItem.itemTotal());

        OrderItemDto secondItem = result.items().get(1);
        assertEquals(2L, secondItem.productId());
        assertEquals("Product 2", secondItem.productName());
        assertEquals(3, secondItem.quantity());
        assertEquals(new BigDecimal("5.25"), secondItem.unitPrice());
        assertEquals(new BigDecimal("15.75"), secondItem.itemTotal());
    }

    @Test
    void toOrderItemDto_shouldCorrectlyMapOrderItemToOrderItemDto() {
        Product product = createProduct(1L, "Test Product", new BigDecimal("12.99"));
        OrderItem orderItem = createOrderItem(1L, product, 4);

        OrderItemDto result = orderMapper.toOrderItemDto(orderItem);

        assertNotNull(result);
        assertEquals(1L, result.productId());
        assertEquals("Test Product", result.productName());
        assertEquals(4, result.quantity());
        assertEquals(new BigDecimal("12.99"), result.unitPrice());
        assertEquals(new BigDecimal("51.96"), result.itemTotal());
    }

    @Test
    void toOrderEntity_shouldCorrectlyMapOrderRequestDtoToOrderEntity() {
        OrderItemRequestDto item1 = new OrderItemRequestDto(1L, 2);
        OrderItemRequestDto item2 = new OrderItemRequestDto(2L, 1);
        List<OrderItemRequestDto> items = List.of(item1, item2);
        OrderRequestDto orderRequestDto = new OrderRequestDto(items);

        Order result = orderMapper.toOrderEntity(orderRequestDto);

        assertNotNull(result);
        assertNull(result.getId()); // Should be ignored
        assertNotNull(result.getOrderDate()); // Should be set to current time
        assertTrue(result.getOrderDate().isBefore(LocalDateTime.now().plusSeconds(1))); // Within 1 second
        assertNotNull(result.getOrderItems()); // Should be initialized but empty since items are ignored
    }

    @Test
    void toOrderItemEntity_shouldCorrectlyMapOrderItemRequestDtoToOrderItem() {
        OrderItemRequestDto orderItemRequestDto = new OrderItemRequestDto(1L, 5);

        OrderItem result = orderMapper.toOrderItemEntity(orderItemRequestDto);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getOrder());
        assertNull(result.getProduct());
        assertEquals(5, result.getQuantity());
    }

    @Test
    void toOrderDto_whenWithEmptyItems_shouldCorrectlyMapOrderToOrderDto() {
        Order order = new Order();
        order.setId(1L);
        order.setStatus(OrderStatus.SUCCESS);
        order.setOrderDate(LocalDateTime.now());
        order.setOrderItems(new ArrayList<>());

        OrderDto result = orderMapper.toOrderDto(order);

        assertNotNull(result);
        assertEquals(1L, result.orderId());
        assertEquals(OrderStatus.SUCCESS, result.status());
        assertEquals(BigDecimal.ZERO, result.totalPrice());
        assertTrue(result.items().isEmpty());
    }


    private Product createProduct(Long id, String name, BigDecimal price) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        return product;
    }

    private OrderItem createOrderItem(Long id, Product product, Integer quantity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(id);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        return orderItem;
    }
}
