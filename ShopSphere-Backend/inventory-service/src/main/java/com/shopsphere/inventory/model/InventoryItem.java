package com.shopsphere.inventory.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
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
    
    // THE FIX: Changed from String to Long to map accurately to the Artisan's User ID
    private Long supplierId; 
    
    private Integer reorderThreshold;
    
    private Integer supplierLeadTimeDays;

    @Version
    private Long version;
}