package com.shopsphere.order.DTO;
import java.util.List;

public class OrderRequest {
    private String userName;
    private String paymentMode;
    private String shippingAddress;
    private List<OrderItemRequest> items;
    private Double expectedTotal; // THE FIX: Security field

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public List<OrderItemRequest> getItems() { return items; }
    public void setItems(List<OrderItemRequest> items) { this.items = items; }
    
    public Double getExpectedTotal() { return expectedTotal; }
    public void setExpectedTotal(Double expectedTotal) { this.expectedTotal = expectedTotal; }
}