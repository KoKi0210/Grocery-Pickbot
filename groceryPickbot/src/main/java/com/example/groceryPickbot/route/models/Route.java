package com.example.groceryPickbot.route.models;

import com.example.groceryPickbot.order.models.Order;
import jakarta.persistence.*;

@Entity
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "route_coordinates", columnDefinition = "TEXT")
    private String coordinatesJson;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public String getCoordinatesJson() {
        return coordinatesJson;
    }

    public void setCoordinatesJson(String coordinatesJson) {
        this.coordinatesJson = coordinatesJson;
    }
}
