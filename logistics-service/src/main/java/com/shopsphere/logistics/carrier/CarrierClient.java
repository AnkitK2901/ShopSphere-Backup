package com.shopsphere.logistics.carrier;

import com.shopsphere.logistics.entity.Shipment;

public interface CarrierClient {
        Shipment createShipment(String orderId);
        Shipment trackShipment(String trackingNumber);
}
