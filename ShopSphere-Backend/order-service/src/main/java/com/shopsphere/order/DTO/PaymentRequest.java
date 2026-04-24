package com.shopsphere.order.DTO;

import lombok.Data;

@Data
public class PaymentRequest {
    private String paymentMethod; // e.g., "RAZORPAY", "STRIPE", "UPI"
}