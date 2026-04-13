package com.shopsphere.logistics.carrier;

import com.shopsphere.logistics.entity.Shipment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MockShiprocketClient implements CarrierClient {

    @Override
    public Shipment createShipment(String orderId) {
        Shipment shipment = new Shipment();
        shipment.setCarrier("MOCK_SHIPROCKET");
        shipment.setTrackingNumber("SR" + System.currentTimeMillis());
        shipment.setTrackingUrl("https://mock-shiprocket/track");
        shipment.setEstimatedDelivery(LocalDate.now().plusDays(3));
        return shipment;
    }

    @Override
    public Shipment trackShipment(String trackingNumber) {
        return null;
    }
}
