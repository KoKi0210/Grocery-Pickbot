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
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing orders.
 */
@Service
public class OrderServiceImpl implements OrderService {

  private final OrderRepository orderRepository;
  private final ProductRepository productRepository;
  private final OrderMapper orderMapper;

  /**
   * Constructs an OrderServiceImpl with the specified dependencies.
   *
   * @param orderRepository   the repository for managing orders
   * @param productRepository the repository for managing products
   * @param orderMapper       the mapper for converting between Order entities and DTOs
   */
  public OrderServiceImpl(OrderRepository orderRepository,
                          ProductRepository productRepository,
                          OrderMapper orderMapper) {
    this.orderRepository = orderRepository;
    this.productRepository = productRepository;
    this.orderMapper = orderMapper;
  }

  @Override
  @Transactional
  public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {

    List<MissingItemDto> missingItems = checkStockAvailability(orderRequestDto.items());
    if (!missingItems.isEmpty()) {
      return OrderResponseDto.failed("Insufficient availability", missingItems);
    }

    Order order = createOrderEntity(orderRequestDto);
    Order savedOrder = orderRepository.save(order);

    updateProductStock(orderRequestDto.items());

    return OrderResponseDto.success(savedOrder.getId(),
        "Order ready! Please collect it at the desk");
  }

  @Override
  @Transactional()
  public List<OrderDto> getAllOrders() {
    return orderRepository.findAll().stream()
        .map(orderMapper::toOrderDto)
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public OrderDto getOrderById(Long id) {
    Order order = orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException(id));
    return orderMapper.toOrderDto(order);
  }

  private Order createOrderEntity(OrderRequestDto orderRequest) {
    Order order = orderMapper.toOrderEntity(orderRequest);

    orderRequest.items().forEach(item -> {
      Product product = getProduct(item.productId());
      OrderItem orderItem = orderMapper.toOrderItemEntity(item);
      orderItem.setProduct(product);
      orderItem.setOrder(order);
      orderItem.setQuantity(item.quantity());
      order.getOrderItems().add(orderItem);
    });

    order.setStatus(OrderStatus.SUCCESS);

    return order;
  }

  private Product getProduct(Long productId) {
    return productRepository.findById(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));
  }

  private void updateProductStock(List<OrderItemRequestDto> items) {
    items.forEach(item -> {
      Product product = getProduct(item.productId());
      product.setQuantity(product.getQuantity() - item.quantity());
    });
  }

  private List<MissingItemDto> checkStockAvailability(List<OrderItemRequestDto> items) {
    return items.stream()
        .map(item -> {
          Product product = getProduct(item.productId());
          if (product.getQuantity() < item.quantity()) {
            return new MissingItemDto(
                product.getName(),
                item.quantity(),
                product.getQuantity()
            );
          }
          return null;
        })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }
}
