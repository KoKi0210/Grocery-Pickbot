package com.example.groceryPickbot.route.services;

import com.example.groceryPickbot.exceptions.OrderNotFoundException;
import com.example.groceryPickbot.order.model.Order;
import com.example.groceryPickbot.order.model.OrderItem;
import com.example.groceryPickbot.product.model.Product;
import com.example.groceryPickbot.route.model.Route;
import com.example.groceryPickbot.route.model.RouteResponse;
import com.example.groceryPickbot.route.repository.RouteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

        ObjectMapper mapper = new ObjectMapper();
        int[][] coordinates = null;
        try {
            coordinates = mapper.readValue(route.get().getCoordinatesJson(), int[][].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

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
        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
             json = mapper.writeValueAsString(fullPath);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Route route = new Route();
        route.setOrder(order);
        route.setCoordinatesJson(json);
        routeRepository.save(route);
    }

    private static List<int[]> calculateOptimalRoute(List<Product> products) {
        List<int[]> route = new ArrayList<>();
        int[] current = {0, 0};
        route.add(current.clone()); // добавяме [0, 0]

        List<int[]> productLocations = products.stream()
                .map(p -> new int[]{p.getLocation().getX(), p.getLocation().getY()})
                .collect(Collectors.toList()); // взимаме локациите на продуктите

        while (!productLocations.isEmpty()) {
            int[] nearest = findNearest(current, productLocations); // Намираме най-близката следваща лоакция на продукт
            route.addAll(generateRouteBetween(current, nearest)); // Добавяме пътя от сегашната локация до следващата най-близка
            productLocations.remove(nearest); // Премахваме най-близката локация от списъка
            current = nearest; // Задаваме най-близката локация да е сегашната
        }

        route.addAll(generateRouteBetween(current, new int[]{0, 0})); // След като сме на последната локация се връщаме на [0, 0]

        return route;

    }

    private static List<int[]> generateRouteBetween(int[] start, int[] end) {
        List<int[]> segment = new ArrayList<>();
        int[] current = start.clone(); // Запазваме локацията, в която сме в момента

        while (!Arrays.equals(current, end)) {
            // Движение по X ос
            if (current[0] != end[0]) {
                current[0] += (end[0] > current[0]) ? 1 : -1;
            }
            // Движение по Y ос
            else {
                current[1] += (end[1] > current[1]) ? 1 : -1;
            }
            segment.add(current.clone()); // добавяме стъпка
        }

        return segment;
    }

    private static int[] findNearest(int[] current, List<int[]> locations) {
        double minDistance = Double.MAX_VALUE;
        int[] nearest = null;

        for (int[] loc : locations) {
            double distance = Math.abs(loc[0] - current[0]) + Math.abs(loc[1] - current[1]); // изчислява разтоянието

            if (distance < minDistance) {
                minDistance = distance;
                nearest = loc; // Задаваме най-близката локация
            }
        }

        return nearest;
    }
}
