package com.shopsphere.order.DTO;

import com.shopsphere.order.Enums.OrderStatus;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {
    private Long orderId;
    private Long customerId;
    private OrderStatus orderStatus;
    private Double totalOrderAmount;
    private String shippingAddress;
    private List<OrderItemResponse> items;
    private List<String> customizationDetails;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String trackingUrl;
    private String carrier;
    private String cancellationReason;
    // FIX: Changed from String to Long to match the first item in the list
    private Long productId;
    private int quantity;
}