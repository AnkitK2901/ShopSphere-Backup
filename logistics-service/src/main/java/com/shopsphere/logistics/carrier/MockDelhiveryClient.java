package com.shopsphere.logistics.carrier;

import com.shopsphere.logistics.entity.Shipment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MockDelhiveryClient implements CarrierClient {

    @Override
    public Shipment createShipment(String orderId) {
        Shipment shipment = new Shipment();
        shipment.setCarrier("MOCK_DELHIVERY");
        shipment.setTrackingNumber("DLV" + System.currentTimeMillis());
        shipment.setTrackingUrl("https://mock-delhivery/track");
        shipment.setEstimatedDelivery(LocalDate.now().plusDays(4));
        return shipment;
    }

    @Override
    public Shipment trackShipment(String trackingNumber) {
        return null;
    }
}
