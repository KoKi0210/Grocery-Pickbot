package com.example.grocerypickbot.route.services;

import com.example.grocerypickbot.bot.configuration.BotProperties;
import com.example.grocerypickbot.bot.models.BotAvailability;
import com.example.grocerypickbot.exceptions.OrderNotFoundException;
import com.example.grocerypickbot.order.models.Order;
import com.example.grocerypickbot.order.models.OrderItem;
import com.example.grocerypickbot.order.repositories.OrderRepository;
import com.example.grocerypickbot.product.mappers.ProductMapper;
import com.example.grocerypickbot.product.models.Product;
import com.example.grocerypickbot.route.models.Route;
import com.example.grocerypickbot.route.models.RouteResponse;
import com.example.grocerypickbot.route.repositories.RouteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing routes.
 */
@Service
public class RouteServiceImpl implements RouteService {
  private static final Location FINAL_LOCATION = new Location(0, 0);
  private static final Logger LOGGER = LoggerFactory.getLogger(RouteServiceImpl.class);
  private static final ProductInfo EMPTY_PRODUCT =
      new ProductInfo("", new Location(0, 0));
  private final RouteRepository routeRepository;
  private final BotProperties botProperties;
  private final OrderRepository orderRepository;
  private final ObjectMapper mapper;
  private final TaskExecutor taskExecutor;
  private final ProductMapper productMapper;

  /**
   * Constructs a RouteServiceImpl with the specified RouteRepository.
   *
   * @param routeRepository the repository for managing routes
   */
  public RouteServiceImpl(RouteRepository routeRepository, BotProperties botProperties,
                          OrderRepository orderRepository, ObjectMapper mapper,
                          @Qualifier("botTaskExecutor") TaskExecutor taskExecutor,
                          ProductMapper productMapper) {
    this.routeRepository = routeRepository;
    this.botProperties = botProperties;
    this.orderRepository = orderRepository;
    this.mapper = mapper;
    this.taskExecutor = taskExecutor;
    this.productMapper = productMapper;
  }

  @Override
  public List<RouteResponse> getRoute(Long id) {
    List<Route> routes = routeRepository.findByOrderId(id);
    if (routes.isEmpty()) {
      throw new OrderNotFoundException(id);
    }

    List<RouteResponse> routeResponses = new ArrayList<>();

    for (Route route : routes) {
      try {
        Location[] locations = mapper.readValue(route.getCoordinatesJson(), Location[].class);
        List<int[]> coordinates = Arrays.stream(locations)
            .map(loc -> new int[]{loc.x(), loc.y()})
            .collect(Collectors.toList());
        routeResponses.add(new RouteResponse(
            id,
            route.getOrder().getStatus(),
            coordinates,
            route.getRouteName()
        ));
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }
    return routeResponses;
  }

  /**
   * Calculates and saves the optimal path for the given order.
   *
   * @param orderId find the order for which to calculate the path
   */
  public void calculateAndSavePath(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new OrderNotFoundException(orderId));

    List<Product> products = order.getOrderItems().stream()
        .map(OrderItem::getProduct)
        .collect(Collectors.toList());

    List<Location> fullPath = calculateOptimalRoute(products);

    String productNames = products.stream()
        .map(Product::getName)
        .collect(Collectors.joining(", "));
    saveRoute(fullPath, order, productNames);
    LOGGER.info("All items collected for order {}", orderId);
  }

  private List<Location> calculateOptimalRoute(List<Product> products) {
    List<Location> route = new ArrayList<>();

    BotAvailability bot = botProperties.getBots().get(0);
    Location
        current =
        new Location(bot.getBot().defaultLocation().getX(), bot.getBot().defaultLocation().getY());
    route.add(current); // add current bot location

    List<Location> productLocations = products.stream()
        .map(p -> new Location(p.getLocation().getX(), p.getLocation().getY()))
        .collect(Collectors.toList()); // get product locations

    while (!productLocations.isEmpty()) {
      Location
          nearest =
          findNearest(current, productLocations); // Find the nearest next product location
      route.addAll(generateRouteBetween(current,
          nearest)); // Add the path from the current location to the next nearest
      productLocations.remove(nearest); // Remove the nearest location from the list
      current = nearest; // Set the nearest location as the current one
    }
    route.addAll(generateRouteToFinalLocation(current));
    // After reaching the last location, return to [0, 0]

    return route;

  }

  private static List<Location> generateRouteBetween(Location start, Location end) {
    List<Location> segments = new ArrayList<>();
    int[] current = {start.x(), start.y()}; // Save the location where we are currently
    int[] target = {end.x(), end.y()}; // Save the location where we want to go

    while (!Arrays.equals(current, target)) {
      if (current[0] != end.x) {
        current[0] += (end.x > current[0]) ? 1 : -1; // Move along the X axis
      } else {
        current[1] += (end.y > current[1]) ? 1 : -1; // Move along the Y axis
      }
      segments.add(new Location(current[0], current[1])); // add a step
    }

    return segments;
  }

  private List<Location> generateRouteToFinalLocation(Location start) {
    return generateRouteBetween(start, FINAL_LOCATION);
  }

  private static Location findNearest(Location current, List<Location> locations) {
    double minDistance = Double.MAX_VALUE;
    Location nearest = null;

    for (Location loc : locations) {
      double
          distance =
          Math.abs(loc.x() - current.x()) + Math.abs(loc.y() - current.y()); // Manhattan distance
      if (distance < minDistance) {
        minDistance = distance;
        nearest = loc; // set nearest location
      }
    }

    return nearest;
  }

  /**
   * Collects items for the specified order in parallel using available bots.
   *
   * @param orderId the ID of the order to collect items for
   */
  public void collectItemsParallel(Long orderId) {
    Order order = orderRepository.findById(orderId)
        .orElseThrow(() -> new RuntimeException("Order not found"));
    List<ProductInfo> products = getProductInfos(order);
    List<ProductInfo> productsSortedByLocation = sortProductsByLocation(products);
    Deque<PairOfProducts> productPairs = createProductPairs(productsSortedByLocation);
    BlockingQueue<BotAvailability> availableBots = getAvailableBots();

    if (availableBots.isEmpty()) {
      throw new RuntimeException("No available bots");
    }

    CountDownLatch latch = new CountDownLatch(productPairs.size());

    assignProductPairsToBots(productPairs, availableBots, order, latch);

    try {
      latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    LOGGER.info("All items collected for order {}", orderId);
  }

  private void assignProductPairsToBots(Deque<PairOfProducts> productPairs,
                                        BlockingQueue<BotAvailability> availableBots,
                                        Order order,
                                        CountDownLatch latch) {
    while (!productPairs.isEmpty()) {
      taskExecutor.execute(() -> {
        try {
          PairOfProducts pairOfProducts = productPairs.poll();
          if (pairOfProducts != null) {
            BotAvailability bot = availableBots.poll(5, TimeUnit.SECONDS);
            if (bot != null) {
              StringBuilder sb = new StringBuilder();
              sb.append("Bot ")
                  .append(bot.getBot().id())
                  .append(" is collecting products: ")
                  .append(pairOfProducts.product1.name);
              if (!pairOfProducts.product2.name.isEmpty()) {
                sb.append(", ").append(pairOfProducts.product2.name);
              }
              LOGGER.info(sb.toString());
              bot.getAvailable().set(false);

              Location botLocation = new Location(
                  bot.getBot().defaultLocation().getX(),
                  bot.getBot().defaultLocation().getY()
              );
              Location firstProductLocation = new Location(
                  pairOfProducts.product1.location.x,
                  pairOfProducts.product1.location.y
              );
              List<Location> route = new ArrayList<>();
              route.add(botLocation);
              route.addAll(generateRouteBetween(botLocation, firstProductLocation));
              checkIfSecondProductIsEmptyAndSaveRoute(order,
                  pairOfProducts,
                  route,
                  firstProductLocation);
              bot.getAvailable().set(true);
              availableBots.put(bot);
              latch.countDown();
            } else {
              productPairs.addFirst(pairOfProducts);
            }
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
      });
    }
  }

  private void checkIfSecondProductIsEmptyAndSaveRoute(Order order,
                                                       PairOfProducts pairOfProducts,
                                                       List<Location> route,
                                                       Location firstProductLocation) {
    if (!pairOfProducts.product2.name.isEmpty()) {
      Location secondProductLocation = new Location(
          pairOfProducts.product2.location.x,
          pairOfProducts.product2.location.y
      );
      route.addAll(generateRouteBetween(firstProductLocation, secondProductLocation));
      route.addAll(generateRouteToFinalLocation(secondProductLocation));
      saveRoute(route,
          order,
          pairOfProducts.product1.name + " " + pairOfProducts.product2.name);
    } else {
      route.addAll(generateRouteToFinalLocation(firstProductLocation));
      saveRoute(route, order, pairOfProducts.product1.name);
    }
  }

  /**
   * Record representing product information including name and location.
   *
   * @param name     the name of the product
   * @param location the location of the product
   */
  public record ProductInfo(String name, Location location) {
  }

  /**
   * Record representing a location with x and y coordinates.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public record Location(int x, int y) {
  }

  private record PairOfProducts(ProductInfo product1, ProductInfo product2) {
  }

  private BlockingQueue<BotAvailability> getAvailableBots() {
    return new LinkedBlockingQueue<>(botProperties.getBots().stream()
        .filter(bot -> bot.getAvailable().get())
        .sorted(
            Comparator.comparingInt((BotAvailability b) -> b.getBot().defaultLocation().getX())
        )
        .collect(Collectors.toCollection(LinkedBlockingQueue::new)));
  }

  private List<ProductInfo> getProductInfos(Order order) {
    return order.getOrderItems().stream()
        .map(item -> productMapper.toProductInfo(item.getProduct()))
        .toList();
  }

  private List<ProductInfo> sortProductsByLocation(List<ProductInfo> products) {
    return products.stream()
        .sorted(Comparator.comparingInt((ProductInfo p) -> p.location().x()))
        .toList();
  }

  private void saveRoute(List<Location> route, Order order, String productName) {
    String json;
    try {
      json = mapper.writeValueAsString(route);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    Route routeToSave = new Route();
    routeToSave.setOrder(order);
    routeToSave.setCoordinatesJson(json);
    routeToSave.setRouteName(productName);
    routeRepository.save(routeToSave);
  }

  private Deque<PairOfProducts> createProductPairs(List<ProductInfo> products) {
    Deque<PairOfProducts> pairs = new ConcurrentLinkedDeque<>();
    if (products.size() > getAvailableBots().size()) {
      for (int i = 0; i < products.size(); i += 2) {
        ProductInfo product1 = products.get(i);
        ProductInfo product2 = (i + 1 < products.size())
            ? products.get(i + 1) : EMPTY_PRODUCT;
        pairs.add(new PairOfProducts(product1, product2));
      }
    } else {
      for (ProductInfo product : products) {
        pairs.add(new PairOfProducts(product, EMPTY_PRODUCT));
      }
    }
    return pairs;
  }
}
