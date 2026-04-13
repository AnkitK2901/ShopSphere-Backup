package com.shopsphere.catalog.Entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;
    private Double basePrice;
    private String previewImage;

    @Transient // Real-time calculated price (not stored in DB)
    private Double totalPrice = 0.0;

    @ManyToMany
    @JoinTable(
            name = "product_selected_options",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<CustomOption> customOptions = new ArrayList<>();

    // FEATURE: Real-time pricing update logic
    @PostLoad @PrePersist @PreUpdate
    public void calculateTotalPrice() {
        double adjustments = (customOptions == null) ? 0.0 :
                customOptions.stream()
                        .filter(o -> o.getPriceAdjustment() != 0) // Extra safety
                        .mapToDouble(CustomOption::getPriceAdjustment).sum();

        // Ensure basePrice isn't null before adding
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
    public List<CustomOption> getCustomOptions() { return customOptions; }
    public void setCustomOptions(List<CustomOption> customOptions) { this.customOptions = customOptions; }

    public Product() {}
}