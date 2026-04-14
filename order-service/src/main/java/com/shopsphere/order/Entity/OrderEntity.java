package com.shopsphere.order.Entity;

import com.shopsphere.order.Enums.OrderStatus;
import com.shopsphere.order.Enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "Orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private int quantity;
    private Long productId;
    private Long customerId;
    private Double priceAtPurchase;
    private Double totalAmount;

    @Column(columnDefinition = "TEXT")
    private String customizationDetails;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // --- LLD REQUIRED: Payment Gateway Tracking ---
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private String transactionId; // To store Stripe/Razorpay generated IDs
    // ----------------------------------------------

    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if(this.status == null) {
            this.status = OrderStatus.CONFIRMED;
        }
        if(this.paymentStatus == null) {
            this.paymentStatus = PaymentStatus.PENDING;
        }
    }

    @PreUpdate
    protected void onUpdate(){
        this.updatedAt = LocalDateTime.now();
    }
}