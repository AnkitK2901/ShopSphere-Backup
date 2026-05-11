package com.shopsphere.catalog.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;

    // SURGICAL FIX: Prevent DB Truncation 
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    private Double basePrice;

    // SURGICAL FIX: Prevent DB Truncation 
    @Column(columnDefinition = "LONGTEXT")
    private String previewImage;

    private boolean isActive;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "product_selected_options",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private Set<CustomOption> customOptions; // RESTORED TO SET

    // RESTORED YOUR ORIGINAL METHOD
    public Double getTotalPrice() {
        if (customOptions == null || customOptions.isEmpty()) {
            return basePrice != null ? basePrice : 0.0;
        }
        double optionsTotal = customOptions.stream()
                .mapToDouble(CustomOption::getPriceAdjustment)
                .sum();
        return basePrice + optionsTotal;
    }
}