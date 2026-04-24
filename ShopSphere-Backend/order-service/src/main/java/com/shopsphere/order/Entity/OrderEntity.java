package com.shopsphere.order.Entity;

import com.shopsphere.order.Enums.OrderStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private int quantity;

    private String productId;

    private Long customerId;

    private Double priceAtPurchase;
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // --- NEW ADDITION: Safely store customized options ---
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_customizations", joinColumns = @JoinColumn(name = "order_id"))
    @Column(name = "custom_detail")
    private List<String> customizationDetails;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if(this.status == null) {
            this.status = OrderStatus.CONFIRMED;
        }
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Double getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(Double priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public List<String> getCustomizationDetails() { return customizationDetails; }
    public void setCustomizationDetails(List<String> customizationDetails) { this.customizationDetails = customizationDetails; }

    public OrderEntity(){}

    public OrderEntity(LocalDateTime createdAt, Long customerId, Long orderId,
                       Double priceAtPurchase, String productId, int quantity,
                       OrderStatus status, Double totalAmount, LocalDateTime updatedAt,
                       List<String> customizationDetails) {
        this.createdAt = createdAt;
        this.customerId = customerId;
        this.orderId = orderId;
        this.priceAtPurchase = priceAtPurchase;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.totalAmount = totalAmount;
        this.updatedAt = updatedAt;
        this.customizationDetails = customizationDetails;
    }
}