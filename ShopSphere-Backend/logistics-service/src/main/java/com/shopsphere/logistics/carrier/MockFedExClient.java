package com.shopsphere.logistics.carrier;

import com.shopsphere.logistics.entity.Shipment;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class MockFedExClient implements CarrierClient {
    @Override
    public Shipment createShipment(String orderId) {
        Shipment shipment = new Shipment();
        shipment.setCarrier("FedEx");
        shipment.setTrackingNumber("FX" + System.currentTimeMillis());
        shipment.setTrackingUrl("https://www.fedex.com/fedextrack/");
        shipment.setEstimatedDelivery(LocalDate.now().plusDays(2));
        return shipment;
    }
}