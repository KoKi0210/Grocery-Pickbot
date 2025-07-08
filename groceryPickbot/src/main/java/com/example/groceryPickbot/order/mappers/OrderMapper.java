package com.example.groceryPickbot.order.mappers;

import com.example.groceryPickbot.order.dtos.OrderDTO;
import com.example.groceryPickbot.order.dtos.OrderItemDTO;
import com.example.groceryPickbot.order.dtos.OrderItemRequestDTO;
import com.example.groceryPickbot.order.dtos.OrderRequestDTO;
import com.example.groceryPickbot.order.models.Order;
import com.example.groceryPickbot.order.models.OrderItem;
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
                .reduce(BigDecimal.ZERO, BigDecimal::add); //започва от 0 и добавя всекя цена за продуктите
    }

    default BigDecimal calculateItemTotal(OrderItem item) {
        return item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())) ; // пресмята цена
    }
}
