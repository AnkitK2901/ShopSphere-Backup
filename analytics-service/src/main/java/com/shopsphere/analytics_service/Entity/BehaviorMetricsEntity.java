package com.shopsphere.analytics_service.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "BehaviorMetrics")
public class BehaviorMetricsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long metricsId;

    private int totalOrders;
    private int repeatPurchaseCount;
    private int abandonedCartCount;
    private String favouriteProduct;
    private double averageOrderValue;

    public Long getMetricsId() { return metricsId; }
    public void setMetricsId(Long metricsId) { this.metricsId = metricsId; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public int getRepeatPurchaseCount() { return repeatPurchaseCount; }
    public void setRepeatPurchaseCount(int repeatPurchaseCount) { this.repeatPurchaseCount = repeatPurchaseCount; }

    public int getAbandonedCartCount() { return abandonedCartCount; }
    public void setAbandonedCartCount(int abandonedCartCount) { this.abandonedCartCount = abandonedCartCount; }

    public String getFavouriteProduct() { return favouriteProduct; }
    public void setFavouriteProduct(String favouriteProduct) { this.favouriteProduct = favouriteProduct; }

    public double getAverageOrderValue() { return averageOrderValue; }
    public void setAverageOrderValue(double averageOrderValue) { this.averageOrderValue = averageOrderValue; }

    public BehaviorMetricsEntity() {}
}