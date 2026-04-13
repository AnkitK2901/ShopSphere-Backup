package com.shopsphere.logistics.entity;

public enum ShipmentStatus {
    CREATED,
    PICKED_UP,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED;

    public boolean canTransitionTo(ShipmentStatus nextStatus) {
        return switch (this) {
            case CREATED -> nextStatus == PICKED_UP;
            case PICKED_UP -> nextStatus == IN_TRANSIT;
            case IN_TRANSIT -> nextStatus == OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> nextStatus == DELIVERED;
            case DELIVERED -> false;
        };
    }
}
