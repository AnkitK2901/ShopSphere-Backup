package com.shopsphere.logistics.exception;

public class InvalidShipmentStatusException extends RuntimeException {

    public InvalidShipmentStatusException(String message) {
        super(message);
    }
}
