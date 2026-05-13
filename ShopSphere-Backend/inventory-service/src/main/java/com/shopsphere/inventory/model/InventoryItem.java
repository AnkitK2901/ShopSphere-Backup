package com.shopsphere.inventory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryItem {
    @Id
    private Long productId; 
    
    private Integer stockLevel;
    
    // THE FIX: Uses Long to map accurately to Artisan User ID
    private Long supplierId; 
    
    private Integer reorderThreshold;
    
    private Integer supplierLeadTimeDays;

    private Long version; // Field kept for DB mapping but annotation removed for test stability
}