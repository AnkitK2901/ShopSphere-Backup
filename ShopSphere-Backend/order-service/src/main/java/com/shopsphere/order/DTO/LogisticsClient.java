package com.shopsphere.order.DTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "logistics-service", path = "/api/shipments")
public interface LogisticsClient {

    @PostMapping("/createShipment/{orderId}")
    ShipmentResponse createShipment(@PathVariable("orderId") Long orderId);

    @GetMapping("/order/{orderId}")
    ShipmentResponse getByOrderId(@PathVariable("orderId") Long orderId);
}