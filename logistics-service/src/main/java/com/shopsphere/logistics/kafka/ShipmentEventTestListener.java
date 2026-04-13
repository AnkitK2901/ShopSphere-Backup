package com.shopsphere.logistics.kafka;

import com.shopsphere.logistics.event.ShipmentEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ShipmentEventTestListener {

    @KafkaListener(topics = "shipment-events", groupId = "test-group")
    public void listen(ShipmentEvent event) {
        System.out.println("✅ EVENT RECEIVED: " + event);
    }
}
