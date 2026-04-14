package com.shopsphere.catalog.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    private String name;
    private Double basePrice;
    private Double totalPrice;
    private String previewImage;

    // --- LLD REQUIRED: Multi-Vendor and Regional Support ---
    private String vendorId; // To track which artisan made this
    private String region;   // To track the regional category
    // -------------------------------------------------------

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "product_options",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<CustomOption> customOptions;

    @PrePersist
    @PreUpdate
    public void calculateTotalPrice() {
        this.totalPrice = this.basePrice;
        if (this.customOptions != null) {
            for (CustomOption option : this.customOptions) {
                this.totalPrice += option.getPriceModifier();
            }
        }
    }
}