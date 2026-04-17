package com.shopsphere.catalog.Entity;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;
    private Double basePrice;
    private String previewImage;

    // --- NEW ADDITION: Soft delete flag ---
    private boolean isActive = true;

    @Transient
    private Double totalPrice = 0.0;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "product_selected_options",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private Set<CustomOption> customOptions = new LinkedHashSet<>();

    @PostLoad @PrePersist @PreUpdate
    public void calculateTotalPrice() {
        double adjustments = (customOptions == null) ? 0.0 :
                customOptions.stream()
                        .filter(o -> o.getPriceAdjustment() != 0) 
                        .mapToDouble(CustomOption::getPriceAdjustment).sum();

        this.totalPrice = (this.basePrice != null ? this.basePrice : 0.0) + adjustments;
    }

    // Getters and Setters
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getBasePrice() { return basePrice; }
    public void setBasePrice(Double basePrice) { this.basePrice = basePrice; }
    public String getPreviewImage() { return previewImage; }
    public void setPreviewImage(String previewImage) { this.previewImage = previewImage; }
    public Double getTotalPrice() { return totalPrice; }
    public Set<CustomOption> getCustomOptions() { return customOptions; }
    public void setCustomOptions(Set<CustomOption> customOptions) { this.customOptions = customOptions; }
    
    // --- NEW GETTERS/SETTERS ---
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Product() {}
}