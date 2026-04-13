package com.shopsphere.logistics.controller;

import com.shopsphere.logistics.entity.Shipment;
import com.shopsphere.logistics.service.ShipmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {
    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    public ResponseEntity<List<Shipment>> getAllShipments() {
        List<Shipment> shipments = shipmentService.getAllShipments();

        if (shipments.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(shipments);
    }

    @PostMapping("/createShipment/{orderId}")
    public ResponseEntity<Shipment> createShipment(@PathVariable String orderId) {
        Shipment shipment = shipmentService.createShipment(orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(shipment);
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<Shipment> getByOrderId(@PathVariable String orderId) {
        Shipment shipment = shipmentService.getShipmentByOrderId(orderId);
        return ResponseEntity.ok(shipment);
    }
}