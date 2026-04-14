package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.StockRequest;
import com.shopsphere.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Inventory Management", description = "APIs for checking stock and auto-reordering processes")
public class InventoryController {

    private final InventoryService inventoryService;

    @Operation(summary = "Check if sufficient stock is available")
    @PostMapping("/check")
    public ResponseEntity<Boolean> checkStock(@Valid @RequestBody StockRequest request) {
        log.info("Received POST request to check stock for Product: {}", request.getProductId());
        boolean inStock = inventoryService.checkStock(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(inStock);
    }

    @Operation(summary = "Deduct stock from a product's inventory")
    @PostMapping("/deduct")
    public ResponseEntity<String> deductStock(@Valid @RequestBody StockRequest request) {
        log.info("Received POST request to deduct stock for Product: {}", request.getProductId());
        inventoryService.deductStock(request.getProductId(), request.getQuantity());
        return ResponseEntity
                .ok("Successfully deducted " + request.getQuantity() + " items for product: " + request.getProductId());
    }
}