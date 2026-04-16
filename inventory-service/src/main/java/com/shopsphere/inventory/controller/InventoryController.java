package com.shopsphere.inventory.controller;

import com.shopsphere.inventory.dto.StockRequest;
import com.shopsphere.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@Slf4j
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // Typically called by Order Service - Left relatively open for internal routing
    @PostMapping("/check")
    public ResponseEntity<Boolean> checkStock(@Valid @RequestBody StockRequest request) {
        log.info("Received POST request to check stock for Product: {}", request.getProductId());
        boolean inStock = inventoryService.checkStock(request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(inStock);
    }

    // SECURED: Only Admins can manually add raw inventory directly to the warehouse
    @PostMapping
    public ResponseEntity<?> addInventory(
            @RequestBody com.shopsphere.inventory.model.InventoryItem item,
            @RequestHeader(value = "X-User-Role", defaultValue = "UNKNOWN") String role) {
        
        if (!"ROLE_ADMIN".equals(role)) {
            log.warn("Unauthorized attempt to add inventory by role: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: Only Administrators can add raw inventory.");
        }

        log.info("Received POST request to add inventory for Product: {}", item.getProductId());
        inventoryService.addInventory(item);
        return ResponseEntity.ok("Successfully added inventory for product: " + item.getProductId());
    }

    // Typically called by Order Service upon checkout
    @PostMapping("/deduct")
    public ResponseEntity<String> deductStock(@Valid @RequestBody StockRequest request) {
        log.info("Received POST request to deduct stock for Product: {}", request.getProductId());
        inventoryService.deductStock(request.getProductId(), request.getQuantity());
        return ResponseEntity
                .ok("Successfully deducted " + request.getQuantity() + " items for product: " + request.getProductId());
    }
}