package com.shopsphere.logistics.controller;

import com.shopsphere.logistics.entity.Shipment;
import com.shopsphere.logistics.service.ShipmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map; // NEW: Added import

@RestController
@RequestMapping("/api/shipments")
@Slf4j
public class ShipmentController {
    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    public ResponseEntity<?> getAllShipments(@RequestHeader(value = "X-User-Role", defaultValue = "UNKNOWN") String role) {
        try {
            if (!"ROLE_LOGISTICS".equals(role) && !"ROLE_ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            log.info("Fetching all shipments");
            List<Shipment> shipments = shipmentService.getAllShipments();

            if (shipments.isEmpty()) {
                log.info("No shipments found");
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(shipments);
        } catch (Exception e) {
            log.error("Internal Logistics DB Error: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Logistics System Error: " + e.getMessage());
        }
    }

    @PostMapping("/createShipment/{orderId}")
    public ResponseEntity<?> createShipment(@PathVariable("orderId") Long orderId) {
        try {
            log.info("Received request to create shipment for Order ID: {}", orderId);
            Shipment shipment = shipmentService.createShipment(String.valueOf(orderId));
            log.info("Shipment created successfully for Order ID: {}", orderId);
            return ResponseEntity.status(HttpStatus.CREATED).body(shipment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getByOrderId(@PathVariable("orderId") Long orderId) {
        try {
            log.info("Fetching shipment details for Order ID: {}", orderId);
            Shipment shipment = shipmentService.getShipmentByOrderId(String.valueOf(orderId));
            return ResponseEntity.ok(shipment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // THE FIX: Added the missing endpoint to serve the enriched data to Angular
    @GetMapping("/enriched/order/{orderId}")
    public ResponseEntity<?> getEnrichedByOrderId(@PathVariable("orderId") Long orderId) {
        try {
            log.info("Fetching enriched shipment details for Order ID: {}", orderId);
            Map<String, Object> enrichedData = shipmentService.getEnrichedShipmentByOrderId(String.valueOf(orderId));
            return ResponseEntity.ok(enrichedData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PatchMapping("/order/{orderId}/{status}")
    public ResponseEntity<?> updateStatusByOrderId(
            @PathVariable("orderId") Long orderId,
            @PathVariable("status") String status,
            @RequestParam(value = "carrier", required = false) String carrier,
            @RequestHeader(value = "X-User-Role", defaultValue = "UNKNOWN") String role) {
        try {
            if (!"ROLE_LOGISTICS".equals(role) && !"ROLE_ADMIN".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            log.info("Updating shipment status for Order ID: {} to {} with carrier {}", orderId, status, carrier);
            Shipment shipment = shipmentService.updateShipmentStatusByOrderId(String.valueOf(orderId), status, carrier);
            log.info("Shipment status updated successfully for Order ID: {}", orderId);
            return ResponseEntity.ok(shipment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}