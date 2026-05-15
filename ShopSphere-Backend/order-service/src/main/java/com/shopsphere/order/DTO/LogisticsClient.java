package com.shopsphere.order.DTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "logistics-service", path = "/api/shipments")
public interface LogisticsClient {

    @PostMapping("/createShipment/{orderId}")
    ShipmentResponse createShipment(@PathVariable("orderId") Long orderId);

    @GetMapping("/order/{orderId}")
    ShipmentResponse getByOrderId(@PathVariable("orderId") Long orderId);

    // THE FIX: Changed to PUT to bypass the Feign bug without deleting data!
    @PutMapping("/order/{orderId}/{status}")
    ShipmentResponse updateShipmentStatus(
        @PathVariable("orderId") Long orderId, 
        @PathVariable("status") String status,
        @RequestHeader("X-User-Role") String role
    );
}