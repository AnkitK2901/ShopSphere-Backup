package com.shopsphere.analytics_service.Client;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Flux<Object> getAllOrders() {
        log.info("Fetching orders from Order Service via WebClient");
        return webClientBuilder.build()
                .get()
                .uri("http://order-service/api/orders")
                .retrieve()
                .bodyToFlux(Object.class);
    }
}