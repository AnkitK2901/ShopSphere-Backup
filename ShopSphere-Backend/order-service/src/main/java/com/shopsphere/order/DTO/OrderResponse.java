package com.shopsphere.order.DTO;

import com.shopsphere.order.Enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long orderId;
    private Long customerId;
    private String productId;
    private Double unitPriceAtPurchase;
    private Double totalOrderAmount;
    private OrderStatus orderStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String trackingUrl;
    private String carrier;
    
    // --- NEW ADDITION ---
    private List<String> customizationDetails;

    // Getters and Setters
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Double getTotalOrderAmount() { return totalOrderAmount; }
    public void setTotalOrderAmount(Double totalOrderAmount) { this.totalOrderAmount = totalOrderAmount; }

    public Double getUnitPriceAtPurchase() { return unitPriceAtPurchase; }
    public void setUnitPriceAtPurchase(Double unitPriceAtPurchase) { this.unitPriceAtPurchase = unitPriceAtPurchase; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getTrackingUrl() { return trackingUrl; }
    public void setTrackingUrl(String trackingUrl) { this.trackingUrl = trackingUrl; }

    public List<String> getCustomizationDetails() { return customizationDetails; }
    public void setCustomizationDetails(List<String> customizationDetails) { this.customizationDetails = customizationDetails; }
}