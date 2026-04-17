package com.shopsphere.logistics.controller;

import com.shopsphere.logistics.entity.Shipment;
import com.shopsphere.logistics.service.ShipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@Slf4j
public class ShipmentController {
    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    public ResponseEntity<List<Shipment>> getAllShipments() {
        log.info("Fetching all shipments");
        List<Shipment> shipments = shipmentService.getAllShipments();

        if (shipments.isEmpty()) {
            log.info("No shipments found");
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(shipments);
    }

    @PostMapping("/createShipment/{orderId}")
    public ResponseEntity<Shipment> createShipment(@PathVariable String orderId) {
        log.info("Received request to create shipment for Order ID: {}", orderId);
        Shipment shipment = shipmentService.createShipment(orderId);
        log.info("Shipment created successfully for Order ID: {}", orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(shipment);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Shipment> getByOrderId(@PathVariable String orderId) {
        log.info("Fetching shipment details for Order ID: {}", orderId);
        Shipment shipment = shipmentService.getShipmentByOrderId(orderId);
        return ResponseEntity.ok(shipment);
    }

    @PatchMapping("/order/{orderId}/{status}")
    public ResponseEntity<Shipment> updateStatusByOrderId(
            @PathVariable String orderId,
            @PathVariable String status) {
        
        log.info("Updating shipment status for Order ID: {} to {}", orderId, status);
        Shipment shipment = shipmentService.updateShipmentStatusByOrderId(orderId, status);
        log.info("Shipment status updated successfully for Order ID: {}", orderId);
        return ResponseEntity.ok(shipment);
    }
}