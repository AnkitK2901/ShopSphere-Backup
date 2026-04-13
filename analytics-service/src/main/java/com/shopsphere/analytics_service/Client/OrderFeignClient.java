package com.shopsphere.analytics_service.Client;

import com.shopsphere.analytics_service.Config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

@FeignClient(name = "order-service", configuration = FeignConfig.class)
public interface OrderFeignClient {

    record OrderResponse(
            Long orderId,
            Long customerId,
            Long productId,
            Double unitPriceAtPurchase,
            Double totalOrderAmount,
            String orderStatus,          // OrderStatus enum → String over JSON
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {}

    // Requires new endpoint added to order-service — see Order Service section below
    @GetMapping("/api/orders/customer/{customerId}")
    List<OrderResponse> getOrdersByCustomerId(@PathVariable("customerId") Long customerId);
}
