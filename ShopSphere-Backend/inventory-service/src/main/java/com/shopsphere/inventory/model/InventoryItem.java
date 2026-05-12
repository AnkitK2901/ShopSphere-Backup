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
    private Long productId; // CHANGED FROM String TO Long
    
    private Integer stockLevel;
    
    private String supplierId;
    
    private Integer reorderThreshold;
    
    private Integer supplierLeadTimeDays;

    @Version
    private Long version;
}