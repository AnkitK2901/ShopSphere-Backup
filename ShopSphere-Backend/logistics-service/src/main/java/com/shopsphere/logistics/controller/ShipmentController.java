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
    public ResponseEntity<?> getAllShipments() {
        try {
            log.info("Fetching all shipments");
            List<Shipment> shipments = shipmentService.getAllShipments();

            if (shipments.isEmpty()) {
                log.info("No shipments found");
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(shipments);
        } catch (Exception e) {
            log.error("Internal Logistics DB Error: ", e);
            // This exposes the EXACT crash reason to your F12 console instead of hiding it behind a 500.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Logistics System Error: " + e.getMessage());
        }
    }

    // FIX: Changed String orderId to Long orderId to align cross-service variable types
    @PostMapping("/createShipment/{orderId}")
    public ResponseEntity<?> createShipment(@PathVariable Long orderId) {
        try {
            log.info("Received request to create shipment for Order ID: {}", orderId);
            Shipment shipment = shipmentService.createShipment(String.valueOf(orderId));
            log.info("Shipment created successfully for Order ID: {}", orderId);
            return ResponseEntity.status(HttpStatus.CREATED).body(shipment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // FIX: Changed String orderId to Long orderId
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getByOrderId(@PathVariable Long orderId) {
        try {
            log.info("Fetching shipment details for Order ID: {}", orderId);
            Shipment shipment = shipmentService.getShipmentByOrderId(String.valueOf(orderId));
            return ResponseEntity.ok(shipment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // FIX: Changed String orderId to Long orderId
    @PatchMapping("/order/{orderId}/{status}")
    public ResponseEntity<?> updateStatusByOrderId(
            @PathVariable Long orderId,
            @PathVariable String status) {
        try {
            log.info("Updating shipment status for Order ID: {} to {}", orderId, status);
            Shipment shipment = shipmentService.updateShipmentStatusByOrderId(String.valueOf(orderId), status);
            log.info("Shipment status updated successfully for Order ID: {}", orderId);
            return ResponseEntity.ok(shipment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}