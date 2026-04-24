package com.shopsphere.order.DTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "logistics-service")
public interface LogisticsClient {

    @PostMapping("/api/shipments/createShipment/{orderId}")
    public ShipmentResponse createShipment(@PathVariable("orderId") String orderId);

    @GetMapping("/api/shipments/order/{orderId}")
    public ShipmentResponse getByOrderId(@PathVariable("orderId") String orderId);
}