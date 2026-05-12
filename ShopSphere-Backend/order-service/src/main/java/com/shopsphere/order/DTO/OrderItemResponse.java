package com.shopsphere.order.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {
    // FIX: Changed from String to Long
    private Long productId; 
    private int quantity;
    private Double price;
    
    // FIX: Added missing field to capture customization in responses
    private String selectedOption; 
}