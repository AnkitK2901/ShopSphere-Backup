package com.shopsphere.order.DTO;

import java.util.List;

public class OrderRequest {

    private String userName;
    private String paymentMode;
    
    // THE FIX: Accept the entire cart at once
    private List<OrderItemRequest> items;

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
}