package com.shopsphere.catalog.Entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "custom_option")
public class CustomOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;            // e.g., "Color", "Size", "Material"
    private String value;           // e.g., "Crimson Red", "XL", "Leather"
    private double priceAdjustment; // e.g., 150.00

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
    public double getPriceAdjustment() { return priceAdjustment; }
    public void setPriceAdjustment(double priceAdjustment) { this.priceAdjustment = priceAdjustment; }

    public CustomOption() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomOption)) return false;
        CustomOption that = (CustomOption) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}