package com.example.groceryPickbot.order.services;

import com.example.groceryPickbot.exceptions.OrderNotFoundException;
import com.example.groceryPickbot.exceptions.ProductNotFoundException;
import com.example.groceryPickbot.order.dto.*;
import com.example.groceryPickbot.order.mapper.OrderMapper;
import com.example.groceryPickbot.order.model.*;
import com.example.groceryPickbot.order.repository.OrderRepository;
import com.example.groceryPickbot.product.model.Product;
import com.example.groceryPickbot.product.repository.ProductRepository;
import com.example.groceryPickbot.route.services.RouteServiceImpl;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final RouteServiceImpl routeService;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            ProductRepository productRepository,
                            RouteServiceImpl routeService,
                            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.routeService = routeService;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderResponseDTO createOrder(OrderRequestDTO orderRequestDTO) {

        List<MissingItemDTO> missingItems = checkStockAvailability(orderRequestDTO.items());
        if (!missingItems.isEmpty()){
            return OrderResponseDTO.failed("Insufficient availability", missingItems);
        }

        Order order = createOrderEntity(orderRequestDTO);
        Order savedOrder = orderRepository.save(order);

        routeService.calculateAndSavePath(savedOrder);
        updateProductStock(orderRequestDTO.items());

        return OrderResponseDTO.success(savedOrder.getId(), "Order ready! Please collect it at the desk");
    }

    @Override
    @Transactional()
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toOrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id).
                orElseThrow(()-> new OrderNotFoundException(id));
        return orderMapper.toOrderDTO(order);
    }

    private Order createOrderEntity(OrderRequestDTO orderRequest) {
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

    private Product getProduct(Long productId){
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private void updateProductStock(List<OrderItemRequestDTO> items){
        items.forEach(item -> {
            Product product = getProduct(item.productId());
            product.setQuantity(product.getQuantity() - item.quantity());
        });
    }

    private List<MissingItemDTO> checkStockAvailability(List<OrderItemRequestDTO> items){
        return items.stream()
                .map(item -> {
                    Product product = getProduct(item.productId());
                    if (product.getQuantity() < item.quantity()) {
                        return new MissingItemDTO(
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
