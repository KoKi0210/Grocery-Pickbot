package com.example.groceryPickbot.route.services;

import com.example.groceryPickbot.exceptions.OrderNotFoundException;
import com.example.groceryPickbot.order.model.Order;
import com.example.groceryPickbot.order.model.OrderItem;
import com.example.groceryPickbot.product.model.Product;
import com.example.groceryPickbot.route.model.Route;
import com.example.groceryPickbot.route.model.RouteResponse;
import com.example.groceryPickbot.route.repository.RouteRepository;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RouteServiceImpl implements RouteService{
    private RouteRepository routeRepository;

    public RouteServiceImpl(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public RouteResponse getRoute(Long id) {
        Optional<Route> route = routeRepository.findByOrderId(id);
        if (route.isEmpty()){
            throw new OrderNotFoundException(id);
        }

        int[][] coordinates = new Gson().fromJson(
                route.get().getCoordinatesJson(),
                int[][].class
        );

        return new RouteResponse(
                id,
                route.get().getOrder().getStatus(),
                Arrays.asList(coordinates)
        );
    }

    public void calculateAndSavePath(Order order) {
        List<Product> products = order.getOrderItems().stream()
                .map(OrderItem::getProduct)
                .collect(Collectors.toList());

        List<int[]> fullPath = calculateOptimalRoute(products);
        String pathJson = new Gson().toJson(fullPath);

        Route route = new Route();
        route.setOrder(order);
        route.setCoordinatesJson(pathJson);
        routeRepository.save(route);
    }

    private static List<int[]> calculateOptimalRoute(List<Product> products) {
        List<int[]> route = new ArrayList<>();
        int[] current = {0, 0};
        route.add(current.clone());

        List<int[]> productLocations = products.stream()
                .map(p -> new int[]{p.getLocation().getX(), p.getLocation().getY()})
                .distinct()
                .collect(Collectors.toList());

        while (!productLocations.isEmpty()) {
            int[] nearest = findNearest(current, productLocations);
            route.addAll(generateRouteBetween(current, nearest));
            productLocations.remove(nearest);
            current = nearest;
        }

        // Връщане в началото
        route.addAll(generateRouteBetween(current, new int[]{0, 0}));

        return route;

    }

    private static List<int[]> generateRouteBetween(int[] start, int[] end) {
        List<int[]> segment = new ArrayList<>();
        int[] current = start.clone();

        while (!Arrays.equals(current, end)) {
            // Движение по X ос
            if (current[0] != end[0]) {
                current[0] += (end[0] > current[0]) ? 1 : -1;
            }
            // Движение по Y ос
            else {
                current[1] += (end[1] > current[1]) ? 1 : -1;
            }
            segment.add(current.clone());
        }

        return segment;
    }

    private static int[] findNearest(int[] current, List<int[]> locations) {
        double minDistance = Double.MAX_VALUE;
        int[] nearest = null;

        for (int[] loc : locations) {
            double distance = Math.sqrt(
                    Math.pow(loc[0] - current[0], 2) +
                            Math.pow(loc[1] - current[1], 2)
            );
            if (distance < minDistance) {
                minDistance = distance;
                nearest = loc;
            }
        }

        return nearest;
    }
}
