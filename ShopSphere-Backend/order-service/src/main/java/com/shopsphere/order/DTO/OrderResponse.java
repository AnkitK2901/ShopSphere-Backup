package com.shopsphere.order.DTO;

import com.shopsphere.order.Enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {

    private Long orderId;
    private Long customerId;
    private String productId; 
    private int quantity; 
    private OrderStatus orderStatus;
    private Double unitPriceAtPurchase;
    private Double totalOrderAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> customizationDetails;
    private String trackingUrl;
    private String carrier;

    // THE UPGRADE: The full list of items in the order
    private List<OrderItemResponse> items;

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }
    public Double getUnitPriceAtPurchase() { return unitPriceAtPurchase; }
    public void setUnitPriceAtPurchase(Double unitPriceAtPurchase) { this.unitPriceAtPurchase = unitPriceAtPurchase; }
    public Double getTotalOrderAmount() { return totalOrderAmount; }
    public void setTotalOrderAmount(Double totalOrderAmount) { this.totalOrderAmount = totalOrderAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<String> getCustomizationDetails() { return customizationDetails; }
    public void setCustomizationDetails(List<String> customizationDetails) { this.customizationDetails = customizationDetails; }
    public String getTrackingUrl() { return trackingUrl; }
    public void setTrackingUrl(String trackingUrl) { this.trackingUrl = trackingUrl; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }
}