package com.shopsphere.analytics_service.Client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Component
public class OrderServiceClient {

    private final WebClient webClient;

    public record OrderResponse(
            Long orderId,
            Long customerId,
            Long productId,
            String orderStatus,
            String createdAt,
            String updatedAt
    ) {}

    public record ProductResponse(
            Long productId,
            String productName,
            double basePrice
    ) {}

    public OrderServiceClient(@Value("${order.service.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<OrderResponse> getOrdersByCustomerId(Long customerId) {
        return webClient.get()
                .uri("/api/orders")
                .retrieve()
                .bodyToFlux(OrderResponse.class)
                .filter(order -> order.customerId().equals(customerId))
                .collectList()
                .block();
    }

    public ProductResponse getProductById(Long productId) {
        try {
            return webClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .bodyToMono(ProductResponse.class)
                    .block();
        } catch (Exception e) {
            System.err.println("[OrderServiceClient] getProductById failed for id "
                    + productId + " → " + e.getMessage());
            return null;
        }
    }

    public boolean customerExists(Long customerId) {
        try {
            webClient.get()
                    .uri("/api/customers/{id}", customerId)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            return true;
        } catch (Exception e) {
            System.err.println("[OrderServiceClient] customerExists failed for id "
                    + customerId + " → " + e.getMessage());
            return false;
        }
    }
}