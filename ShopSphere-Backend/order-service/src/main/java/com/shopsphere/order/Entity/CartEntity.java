package com.shopsphere.order.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "AbandonedCarts")
public class CartEntity {

    @Id
    private Long customerId;

    @Column(columnDefinition = "TEXT")
    private String cartJson;

    private LocalDateTime lastUpdated;

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCartJson() { return cartJson; }
    public void setCartJson(String cartJson) { this.cartJson = cartJson; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}