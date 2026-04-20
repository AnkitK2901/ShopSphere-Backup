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
    private String productId;
    
    private Integer stockLevel;
    
    private String supplierId;
    
    private Integer reorderThreshold;
    
    private Integer supplierLeadTimeDays;
}