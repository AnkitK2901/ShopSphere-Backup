package com.shopsphere.logistics.client;

import java.util.Map;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service")
public interface OrderFeignClient {

    @PatchMapping("/api/orders/{orderId}/status")
    void updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody Map<String, String> statusRequest);

    @GetMapping("/api/orders/status/{status}")
    List<Map<String, Object>> getOrdersByStatus(@PathVariable("status") String status);

    // NEW: Needed by Logistics to fetch items for the Packing Slip UI
    @GetMapping("/api/orders/{orderId}")
    Map<String, Object> getOrderById(@PathVariable("orderId") Long orderId);
}