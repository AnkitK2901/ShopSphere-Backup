package com.shopsphere.order.DTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "inventory-service", path = "/api/inventory")
public interface InventoryClient {
    @PostMapping("/check")
    ResponseEntity<Boolean> checkStock(@RequestBody StockRequest request);

    @PostMapping("/deduct")
    ResponseEntity<String> deductStock(@RequestBody StockRequest request);

    // Add this to InventoryClient.java
    @PostMapping("/refund")
    ResponseEntity<String> refundStock(@RequestBody StockRequest request);
}