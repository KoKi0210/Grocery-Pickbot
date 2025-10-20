package com.example.grocerypickbot.route.services;

import com.example.grocerypickbot.bot.configuration.BotProperties;
import com.example.grocerypickbot.bot.models.Bot;
import com.example.grocerypickbot.bot.models.BotAvailability;
import com.example.grocerypickbot.exceptions.OrderNotFoundException;
import com.example.grocerypickbot.order.models.Order;
import com.example.grocerypickbot.order.models.OrderItem;
import com.example.grocerypickbot.order.models.OrderStatus;
import com.example.grocerypickbot.order.repositories.OrderRepository;
import com.example.grocerypickbot.product.mappers.ProductMapper;
import com.example.grocerypickbot.product.models.Location;
import com.example.grocerypickbot.product.models.Product;
import com.example.grocerypickbot.route.models.Route;
import com.example.grocerypickbot.route.models.RouteResponse;
import com.example.grocerypickbot.route.repositories.RouteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RouteServiceImplTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private BotProperties botProperties;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private TaskExecutor taskExecutor;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private RouteServiceImpl routeService;

    private Order testOrder;
    private Product testProduct1;
    private Product testProduct2;
    private OrderItem testOrderItem1;
    private OrderItem testOrderItem2;
    private Route testRoute;
    private Location location1;
    private Location location2;

    @BeforeEach
    void setUp() {

        location1 = new Location();
        location1.setX(5);
        location1.setY(2);

        testProduct1 = new Product();
        testProduct1.setLocation(location1);
        testProduct1.setName("Product1");

        location2 = new Location();
        location2.setX(1);
        location2.setY(5);

        testProduct2 = new Product();
        testProduct2.setLocation(location2);
        testProduct2.setName("Product2");


        testOrderItem1 = new OrderItem();
        testOrderItem2 = new OrderItem();
        testOrderItem1.setProduct(testProduct1);
        testOrderItem2.setProduct(testProduct2);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setStatus(OrderStatus.SUCCESS);
        testOrder.setOrderItems(List.of(testOrderItem1, testOrderItem2));

        testRoute = new Route();
        testRoute.setId(1L);
        testRoute.setOrder(testOrder);
        testRoute.setRouteName("Test Product");
        testRoute.setCoordinatesJson("[{\"x\":0,\"y\":0},{\"x\":1,\"y\":0},{\"x\":2,\"y\":0}]");
    }

    @Test
    void getRoute_whenValidOrderId_shouldReturnSuccessfulRouteResponses() throws JsonProcessingException {
        Long orderId = 1L;
        List<Route> routes = List.of(testRoute);
        RouteServiceImpl.Location[] locations = {
            new RouteServiceImpl.Location(0, 0),
            new RouteServiceImpl.Location(1, 0),
            new RouteServiceImpl.Location(2, 0)
        };

        when(routeRepository.findByOrderId(orderId)).thenReturn(routes);
        when(mapper.readValue(anyString(), eq(RouteServiceImpl.Location[].class))).thenReturn(locations);

        List<RouteResponse> result = routeService.getRoute(orderId);

        assertNotNull(result);
        assertEquals(1, result.size());
        RouteResponse response = result.get(0);
        assertEquals(orderId, response.orderId());
        assertEquals(OrderStatus.SUCCESS, response.status());
        assertEquals("Test Product", response.routeName());
        assertEquals(3, response.visitedLocations().size());

        verify(routeRepository).findByOrderId(orderId);
        verify(mapper).readValue(anyString(), eq(RouteServiceImpl.Location[].class));
    }

    @Test
    void getRoute_whenOrderNotFound_shouldThrowOrderNotFoundException() {
        Long orderId = 999L;
        when(routeRepository.findByOrderId(orderId)).thenReturn(new ArrayList<>());

        assertThrows(OrderNotFoundException.class, () -> routeService.getRoute(orderId));
        verify(routeRepository).findByOrderId(orderId);
    }

    @Test
    void getRoute_whenJsonProcessingFails_shouldThrowRuntimeException() throws JsonProcessingException {
        Long orderId = 1L;
        List<Route> routes = List.of(testRoute);

        when(routeRepository.findByOrderId(orderId)).thenReturn(routes);
        when(mapper.readValue(anyString(), eq(RouteServiceImpl.Location[].class)))
            .thenThrow(new JsonProcessingException("JSON error") {});

        assertThrows(RuntimeException.class, () -> routeService.getRoute(orderId));
    }

    @Test
    void calculateAndSavePath_whenValidOrderId_shouldCalculateAndSaveRoute() throws JsonProcessingException {
        Long orderId = 1L;
        BotAvailability botAvailability = createMockBotAvailability(0, 0);
        List<BotAvailability> bots = List.of(botAvailability);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(botProperties.getBots()).thenReturn(bots);
        when(mapper.writeValueAsString(any())).thenReturn("[]");

        routeService.calculateAndSavePath(orderId);

        verify(orderRepository).findById(orderId);
        verify(routeRepository).save(any(Route.class));
        verify(mapper).writeValueAsString(any());
    }

    @Test
    void calculateAndSavePath_whenOrderNotFound_shouldThrowOrderNotFoundException() {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> routeService.calculateAndSavePath(orderId));
        verify(orderRepository).findById(orderId);
        verify(routeRepository, never()).save(any());
    }

    @Test
    void collectItemsParallel_whenValidOrderIdAndAvailableBots_shouldCollectItemsSuccessfully() {
        Long orderId = 1L;
        BotAvailability botAvailability = createMockBotAvailability(0, 0);
        List<BotAvailability> bots = List.of(botAvailability);

        RouteServiceImpl.ProductInfo productInfo1 = new RouteServiceImpl.ProductInfo("Product1",
                new RouteServiceImpl.Location(5, 2));
        RouteServiceImpl.ProductInfo productInfo2 = new RouteServiceImpl.ProductInfo("Product2",
                new RouteServiceImpl.Location(1, 5));

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(botProperties.getBots()).thenReturn(bots);
        when(productMapper.toProductInfo(testProduct1)).thenReturn(productInfo1);
        when(productMapper.toProductInfo(testProduct2)).thenReturn(productInfo2);

        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        routeService.collectItemsParallel(orderId);

        verify(orderRepository).findById(orderId);
        verify(productMapper).toProductInfo(testProduct1);
        verify(productMapper).toProductInfo(testProduct2);
        verify(taskExecutor, atLeastOnce()).execute(any(Runnable.class));
    }

    @Test
    void collectItemsParallel_whenOrderNotFound_shouldThrowRuntimeException() {
        Long orderId = 999L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> routeService.collectItemsParallel(orderId));
        verify(orderRepository).findById(orderId);
    }

    @Test
    void collectItemsParallel_whenNoAvailableBots_shouldThrowRuntimeException() {
        Long orderId = 1L;
        lenient().when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        lenient().when(botProperties.getBots()).thenReturn(new ArrayList<>());

        assertThrows(RuntimeException.class, () -> routeService.collectItemsParallel(orderId));
        verify(orderRepository).findById(orderId);
    }

    @Test
    void calculateAndSavePath_whenMultipleProducts_shouldCalculateOptimalRoute() throws JsonProcessingException {
        Long orderId = 1L;

        Location location1 = new Location();
        location1.setX(2);
        location1.setY(1);

        Product product1 = new Product();
        product1.setLocation(location1);
        product1.setName("Product 1");

        Location location2 = new Location();
        location2.setX(5);
        location2.setY(3);

        Product product2 = new Product();
        product2.setLocation(location2);
        product2.setName("Product 2");

        OrderItem item1 = new OrderItem();
        item1.setProduct(product1);
        OrderItem item2 = new OrderItem();
        item2.setProduct(product2);

        Order order = new Order();
        order.setId(orderId);
        order.setOrderItems(List.of(item1, item2));

        BotAvailability bot = createMockBotAvailability(0, 0);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(botProperties.getBots()).thenReturn(List.of(bot));
        when(mapper.writeValueAsString(any())).thenReturn("[]");

        routeService.calculateAndSavePath(orderId);

        verify(routeRepository).save(any(Route.class));
        verify(mapper).writeValueAsString(any());
    }

    @Test
    void calculateAndSavePath_whenJsonProcessingFails_shouldThrowRuntimeException() throws JsonProcessingException {
        Long orderId = 1L;
        BotAvailability bot = createMockBotAvailability(0, 0);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(testOrder));
        when(botProperties.getBots()).thenReturn(List.of(bot));
        when(mapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("JSON error") {});

        assertThrows(RuntimeException.class, () -> routeService.calculateAndSavePath(orderId));
    }

    @Test
    void getRoute_whenMultipleRoutes_shouldReturnAllRouteResponses() throws JsonProcessingException {
        Long orderId = 1L;

        Route route1 = new Route();
        route1.setId(1L);
        route1.setOrder(testOrder);
        route1.setRouteName("Product 1");
        route1.setCoordinatesJson("[{\"x\":0,\"y\":0}]");

        Route route2 = new Route();
        route2.setId(2L);
        route2.setOrder(testOrder);
        route2.setRouteName("Product 2");
        route2.setCoordinatesJson("[{\"x\":1,\"y\":1}]");

        List<Route> routes = List.of(route1, route2);

        RouteServiceImpl.Location[] locations1 = {new RouteServiceImpl.Location(0, 0)};
        RouteServiceImpl.Location[] locations2 = {new RouteServiceImpl.Location(1, 1)};

        when(routeRepository.findByOrderId(orderId)).thenReturn(routes);
        when(mapper.readValue(route1.getCoordinatesJson(), RouteServiceImpl.Location[].class))
            .thenReturn(locations1);
        when(mapper.readValue(route2.getCoordinatesJson(), RouteServiceImpl.Location[].class))
            .thenReturn(locations2);

        // Act
        List<RouteResponse> result = routeService.getRoute(orderId);

        // Assert
        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).routeName());
        assertEquals("Product 2", result.get(1).routeName());
    }

    @Test
    void collectItemsParallel_whenProductsSorted_shouldProcessInCorrectOrder() {
        BotAvailability bot = createMockBotAvailability(0, 0);

        RouteServiceImpl.ProductInfo productInfo1 = new RouteServiceImpl.ProductInfo("Product1",
            new RouteServiceImpl.Location(5, 2));
        RouteServiceImpl.ProductInfo productInfo2 = new RouteServiceImpl.ProductInfo("Product2",
            new RouteServiceImpl.Location(1, 5));

        when(orderRepository.findById(testOrder.getId())).thenReturn(Optional.of(testOrder));
        when(botProperties.getBots()).thenReturn(List.of(bot));
        when(productMapper.toProductInfo(testProduct1)).thenReturn(productInfo1);
        when(productMapper.toProductInfo(testProduct2)).thenReturn(productInfo2);
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));

        routeService.collectItemsParallel(testOrder.getId());

        verify(productMapper).toProductInfo(testProduct1);
        verify(productMapper).toProductInfo(testProduct2);
        verify(taskExecutor, atLeastOnce()).execute(any(Runnable.class));
    }

    private BotAvailability createMockBotAvailability(int x, int y) {
        Location location = new Location();
        location.setX(x);
        location.setY(y);

        Bot mockBot = mock(Bot.class);
        when(mockBot.defaultLocation()).thenReturn(location);

        BotAvailability botAvailability = mock(BotAvailability.class);
        when(botAvailability.getBot()).thenReturn(mockBot);
        lenient().when(botAvailability.getAvailable()).thenReturn(new AtomicBoolean(true));

        return botAvailability;
    }
}
