package com.example.groceryPickbot.order.mapper;

import com.example.groceryPickbot.order.dto.OrderDTO;
import com.example.groceryPickbot.order.dto.OrderItemDTO;
import com.example.groceryPickbot.order.dto.OrderItemRequestDTO;
import com.example.groceryPickbot.order.dto.OrderRequestDTO;
import com.example.groceryPickbot.order.model.Order;
import com.example.groceryPickbot.order.model.OrderItem;
import org.mapstruct.*;
import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "totalPrice", expression = "java(calculateTotal(order))")
    @Mapping(target = "items", source = "orderItems")
    OrderDTO toOrderDTO(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "unitPrice", source = "product.price")
    @Mapping(target = "itemTotal", expression = "java(calculateItemTotal(item))")
    OrderItemDTO toOrderItemDTO(OrderItem item);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())")
    Order toOrderEntity(OrderRequestDTO orderRequestDTO);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "product", ignore = true)
    OrderItem toOrderItemEntity(OrderItemRequestDTO orderItemRequestDTO);

    default BigDecimal calculateTotal(Order order) {
        return order.getOrderItems().stream()
                .map(this::calculateItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    default BigDecimal calculateItemTotal(OrderItem item) {
        return BigDecimal.valueOf(item.getProduct().getPrice() * item.getQuantity());
    }
}
