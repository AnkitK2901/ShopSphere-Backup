package com.shopsphere.order.DTO;

public class OrderRequest {

    private String userName;

    private String productId;

    private int quantity;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPaymentMode(String string) {
        throw new UnsupportedOperationException("Unimplemented method 'setPaymentMode'");
    }
}
