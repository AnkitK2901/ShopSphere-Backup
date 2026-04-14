package com.shopsphere.logistics.controller;

import com.shopsphere.logistics.entity.Shipment;
import com.shopsphere.logistics.service.ShipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Logistics Management", description = "APIs for tracking and managing order shipments")
public class ShipmentController {
    
    private final ShipmentService shipmentService;

    @Operation(summary = "Get all shipments across the platform")
    @GetMapping
    public ResponseEntity<List<Shipment>> getAllShipments() {
        log.info("REST request to get all shipments");
        List<Shipment> shipments = shipmentService.getAllShipments();

        if (shipments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(shipments);
    }

    @Operation(summary = "Create a new shipment for an order")
    @PostMapping("/createShipment/{orderId}")
    public ResponseEntity<Shipment> createShipment(@PathVariable String orderId) {
        log.info("REST request to create shipment for order: {}", orderId);
        Shipment shipment = shipmentService.createShipment(orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(shipment);
    }

    @Operation(summary = "Get tracking details for a specific order")
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Shipment> getByOrderId(@PathVariable String orderId) {
        log.info("REST request to get tracking info for order: {}", orderId);
        Shipment shipment = shipmentService.getShipmentByOrderId(orderId);
        return ResponseEntity.ok(shipment);
    }
    
    @Operation(summary = "Manually patch the status of a shipment")
    @PatchMapping("/order/{orderId}/{status}")
    public ResponseEntity<Shipment> updateStatusByOrderId(
            @PathVariable String orderId,
            @PathVariable String status) {
        log.info("REST request to update status of order: {} to {}", orderId, status);
        Shipment shipment = shipmentService.updateShipmentStatusByOrderId(orderId, status);
        return ResponseEntity.ok(shipment);
    }
}