package com.shopsphere.order.DTO;

import com.shopsphere.order.Enums.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderResponse {
    private Long orderId;
    private Long customerId;
    private Long productId;
    private String customizationDetails; // Added field
    private Double unitPriceAtPurchase;
    private Double totalOrderAmount;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String trackingUrl;
    private String carrier;
}