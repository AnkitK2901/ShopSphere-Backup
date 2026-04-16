package com.shopsphere.inventory.service;

import com.shopsphere.inventory.exception.InsufficientStockException;
import com.shopsphere.inventory.exception.ResourceNotFoundException;
import com.shopsphere.inventory.model.InventoryItem;
import com.shopsphere.inventory.repository.InventoryRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public boolean checkStock(String productId, Integer qty) {
        log.info("Checking stock for Product ID: {} | Requested Qty: {}", productId, qty);
        return inventoryRepository.findById(productId)
                .map(item -> item.getStockLevel() >= qty)
                .orElse(false);
    }
    
    @Transactional
    public com.shopsphere.inventory.model.InventoryItem addInventory(
            com.shopsphere.inventory.model.InventoryItem item) {
        return inventoryRepository.save(item);
    }

    @Transactional
    public void deductStock(String productId, Integer qty) {
        log.info("Attempting to deduct {} items from Product ID: {}", qty, productId);
        InventoryItem item = inventoryRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Deduction failed. Product ID {} does not exist in the database.", productId);
                    return new ResourceNotFoundException("Product ID not found: " + productId);
                });

        if (item.getStockLevel() < qty) {
            log.warn("Insufficient stock for Product ID: {}. Requested: {}, Available: {}",
                    productId, qty, item.getStockLevel());
            throw new InsufficientStockException("Not enough stock available!");
        }

        item.setStockLevel(item.getStockLevel() - qty);
        inventoryRepository.save(item);
        log.info("Successfully deducted stock. Product ID: {} | New Stock Level: {}",
                productId, item.getStockLevel());
    }
}