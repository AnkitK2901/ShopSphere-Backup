package com.shopsphere.logistics.entity;

public enum ShipmentStatus {
    CREATED,
    PICKED_UP,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED;

    public boolean canTransitionTo(ShipmentStatus nextStatus) {
        boolean isValid;

        switch (this) {
            case CREATED:
                isValid = nextStatus == PICKED_UP;
                break;
            case PICKED_UP:
                isValid = nextStatus == IN_TRANSIT;
                break;
            case IN_TRANSIT:
                isValid = nextStatus == OUT_FOR_DELIVERY;
                break;
            case OUT_FOR_DELIVERY:
                isValid = nextStatus == DELIVERED;
                break;
            case DELIVERED:
                isValid = false;
                break;
            default:
                isValid = false;
                break;
        }

        return isValid;
    }
}
