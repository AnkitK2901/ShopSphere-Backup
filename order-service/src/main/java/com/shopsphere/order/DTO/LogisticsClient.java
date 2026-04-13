package com.shopsphere.order.DTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "logistics-service", url = "${logistics.service.url}")
public interface LogisticsClient {

    @PostMapping("/api/shipments/createShipment/{orderId}")
    ShipmentResponse createShipment(@PathVariable String orderId);

    @GetMapping("/api/shipments/order/{orderId}")
    ShipmentResponse getByOrderId(@PathVariable("orderId") String orderId);
}
