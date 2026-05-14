package com.shopsphere.catalog.RequestDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {
    private String name;
    private String description; // Safely added
    private Double basePrice;
    private String previewImage;
    private boolean isActive;
    
    // RESTORED YOUR EXACT VARIABLE NAME
    private List<Long> selectedOptionIds; 
    
    private Integer stockLevel;
}