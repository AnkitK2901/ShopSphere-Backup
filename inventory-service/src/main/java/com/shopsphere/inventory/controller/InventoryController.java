package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.StockRequest;
import com.shopsphere.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@Slf4j
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/check")
    public ResponseEntity<Boolean> checkStock(@Valid @RequestBody StockRequest request) {
        log.info("Received POST request to check stock for Product: {}", request.getProductId());
        boolean inStock = inventoryService.checkStock(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(inStock);
    }

    @PostMapping
    public ResponseEntity<String> addInventory(@RequestBody com.shopsphere.inventory.model.InventoryItem item) {
        log.info("Received POST request to add inventory for Product: {}", item.getProductId());
        inventoryService.addInventory(item);
        return ResponseEntity.ok("Successfully added inventory for product: " + item.getProductId());
    }

    @PostMapping("/deduct")
    public ResponseEntity<String> deductStock(@Valid @RequestBody StockRequest request) {
        log.info("Received POST request to deduct stock for Product: {}", request.getProductId());
        inventoryService.deductStock(request.getProductId(), request.getQuantity());
        return ResponseEntity
                .ok("Successfully deducted " + request.getQuantity() + " items for product: " + request.getProductId());
    }
}