package com.example.grocerypickbot.order.mappers;

import com.example.grocerypickbot.order.dtos.OrderDto;
import com.example.grocerypickbot.order.dtos.OrderItemDto;
import com.example.grocerypickbot.order.dtos.OrderItemRequestDto;
import com.example.grocerypickbot.order.dtos.OrderRequestDto;
import com.example.grocerypickbot.order.models.Order;
import com.example.grocerypickbot.order.models.OrderItem;
import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

/**
 * Mapper interface for converting between Order entities and DTOs.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

  /**
   * Maps an Order entity to an OrderDto.
   *
   * @param order the Order entity to be mapped
   * @return the corresponding OrderDto
   */
  @Mapping(target = "orderId", source = "id")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "totalPrice", expression = "java(calculateTotal(order))")
  @Mapping(target = "items", source = "orderItems")
  OrderDto toOrderDto(Order order);

  /**
   * Maps an OrderItem entity to an OrderItemDto.
   *
   * @param item the OrderItem entity to be mapped
   * @return the corresponding OrderItemDto
   */
  @Mapping(target = "productId", source = "product.id")
  @Mapping(target = "productName", source = "product.name")
  @Mapping(target = "unitPrice", source = "product.price")
  @Mapping(target = "itemTotal", expression = "java(calculateItemTotal(item))")
  OrderItemDto toOrderItemDto(OrderItem item);

  /**
   * Maps an OrderRequestDto to an Order entity.
   *
   * @param orderRequestDto the OrderRequestDto to be mapped
   * @return the corresponding Order entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "orderItems", ignore = true)
  @Mapping(target = "orderDate", expression = "java(java.time.LocalDateTime.now())")
  Order toOrderEntity(OrderRequestDto orderRequestDto);

  /**
   * Maps an OrderItemRequestDto to an OrderItem entity.
   *
   * @param orderItemRequestDto the OrderItemRequestDto to be mapped
   * @return the corresponding OrderItem entity
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "order", ignore = true)
  @Mapping(target = "product", ignore = true)
  OrderItem toOrderItemEntity(OrderItemRequestDto orderItemRequestDto);

  /**
   * Calculates the total price of an order by summing the total prices of its items.
   *
   * @param order the Order entity
   * @return the total price of the order
   */
  default BigDecimal calculateTotal(Order order) {
    return order.getOrderItems().stream()
        .map(this::calculateItemTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add); // започва от 0 и добавя всекя цена за продуктите
  }

  /**
   * Calculates the total price for a single order item.
   *
   * @param item the OrderItem entity
   * @return the total price for the item (unit price * quantity)
   */
  default BigDecimal calculateItemTotal(OrderItem item) {
    return item.getProduct()
        .getPrice()
        .multiply(BigDecimal.valueOf(item.getQuantity())); // пресмята цена
  }
}
