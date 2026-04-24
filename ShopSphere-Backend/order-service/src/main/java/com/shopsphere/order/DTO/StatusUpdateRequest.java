package com.shopsphere.order.DTO;

import com.shopsphere.order.Enums.OrderStatus;

public class StatusUpdateRequest {

    private OrderStatus newStatus;

    public OrderStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(OrderStatus newStatus) {
        this.newStatus = newStatus;
    }
}
