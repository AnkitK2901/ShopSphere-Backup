package com.shopsphere.logistics.entity;

public enum ShipmentStatus {
    CREATED,
    PACKED,
    IN_TRANSIT,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED; // FIX: Added CANCELLED

    public boolean canTransitionTo(ShipmentStatus nextStatus) {
        if (nextStatus == CANCELLED) return true; // Can cancel anytime

        boolean isValid;
        switch (this) {
            case CREATED:
                isValid = nextStatus == PACKED;
                break;
            case PACKED:
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