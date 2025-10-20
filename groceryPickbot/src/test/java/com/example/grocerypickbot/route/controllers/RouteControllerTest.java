package com.example.grocerypickbot.route.controllers;

import com.example.grocerypickbot.order.models.OrderStatus;
import com.example.grocerypickbot.route.models.RouteResponse;
import com.example.grocerypickbot.route.services.RouteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RouteControllerTest {

    @Mock
    private RouteServiceImpl routeService;

    @InjectMocks
    private RouteController routeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(routeController).build();
    }

    @Test
    void getRoute_whenCollectInParallelFalse_shouldReturnOkWithRouteResponses() throws Exception {
        Long orderId = 2L;
        List<RouteResponse> mockResponse = List.of(new RouteResponse(orderId, OrderStatus.SUCCESS, List.of(new int[]{1, 2}), "RouteA"));

        when(routeService.getRoute(orderId)).thenReturn(mockResponse);

        mockMvc.perform(get("/routes")
                        .param("orderId", orderId.toString())
                        .param("collectInParallel", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(routeService).getRoute(orderId);
        verify(routeService).calculateAndSavePath(orderId);
        verify(routeService, never()).collectItemsParallel(orderId);
    }

    @Test
    void getRoute_whenCollectInParallelTrue_shouldReturnOkWithRouteResponses() throws Exception {
        Long orderId = 2L;
        List<RouteResponse> mockResponse = List.of(new RouteResponse(orderId, OrderStatus.SUCCESS, List.of(new int[]{1, 2}), "RouteA"));

        when(routeService.getRoute(orderId)).thenReturn(mockResponse);

        mockMvc.perform(get("/routes")
                        .param("orderId", orderId.toString())
                        .param("collectInParallel", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(routeService).getRoute(orderId);
        verify(routeService).collectItemsParallel(orderId);
        verify(routeService, never()).calculateAndSavePath(orderId);
    }

    @Test
    void getRoute_whenNoRoutesFound_shouldReturnOkWithEmptyList() throws Exception {
        Long orderId = 3L;
        when(routeService.getRoute(orderId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/routes")
                        .param("orderId", orderId.toString())
                        .param("collectInParallel", "false")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(routeService).getRoute(orderId);
    }

    @Test
    void getRoute_whenOrderIdParamIsMissing_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/routes")
                        .param("collectInParallel", "false"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRoute_whenCollectInParallelParamIsMissing_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/routes")
                        .param("orderId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRoute_whenOrderIdIsInvalid_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/routes")
                        .param("orderId", "invalid")
                        .param("collectInParallel", "false"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRoute_whenMultipleRoutesAvailable_shouldReturnOkWithAllRoutes() throws Exception {
        Long orderId = 4L;
        List<RouteResponse> mockResponse = List.of(
                new RouteResponse(orderId, OrderStatus.SUCCESS, List.of(new int[]{1, 2}), "RouteA"),
                new RouteResponse(orderId, OrderStatus.SUCCESS, List.of(new int[]{3, 4}), "RouteB"),
                new RouteResponse(orderId, OrderStatus.SUCCESS, List.of(new int[]{5, 6}), "RouteC")
        );

        when(routeService.getRoute(orderId)).thenReturn(mockResponse);

        mockMvc.perform(get("/routes")
                        .param("orderId", orderId.toString())
                        .param("collectInParallel", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)));

        verify(routeService).getRoute(orderId);
        verify(routeService).collectItemsParallel(orderId);
        verify(routeService, never()).calculateAndSavePath(orderId);
    }

    @Test
    void getRoute_whenDifferentOrderIdAndCollectInParallelFalse_shouldReturnOkWithRoutes() throws Exception {
        Long orderId = 100L;
        List<RouteResponse> mockResponse = List.of(new RouteResponse(orderId, OrderStatus.SUCCESS
            , List.of(new int[]{10, 20}), "RouteX"),new RouteResponse(orderId, OrderStatus.SUCCESS
            , List.of(new int[]{1, 2}), "RouteY"));

        when(routeService.getRoute(orderId)).thenReturn(mockResponse);

        mockMvc.perform(get("/routes")
                        .param("orderId", orderId.toString())
                        .param("collectInParallel", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(routeService).getRoute(orderId);
        verify(routeService).calculateAndSavePath(orderId);
    }

    @Test
    void getRoute_whenDifferentOrderIdAndCollectInParallelTrue_shouldReturnOkWithRoutes() throws Exception {
        Long orderId = 200L;
        List<RouteResponse> mockResponse = List.of(new RouteResponse(orderId, OrderStatus.SUCCESS, List.of(new int[]{30, 40}), "RouteY"));

        when(routeService.getRoute(orderId)).thenReturn(mockResponse);

        mockMvc.perform(get("/routes")
                        .param("orderId", orderId.toString())
                        .param("collectInParallel", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(routeService).getRoute(orderId);
        verify(routeService).collectItemsParallel(orderId);
    }
}

