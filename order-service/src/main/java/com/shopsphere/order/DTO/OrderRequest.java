package com.shopsphere.order.DTO;

import lombok.Data;

@Data
public class OrderRequest {
    private String userName;
    private Long productId;
    private int quantity;
    private String paymentMode;
    // Added to receive customization choices from the frontend
    private String customizationDetails; 
}