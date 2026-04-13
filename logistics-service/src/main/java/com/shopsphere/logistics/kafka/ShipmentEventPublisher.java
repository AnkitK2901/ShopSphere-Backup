package com.shopsphere.logistics.kafka;

import com.shopsphere.logistics.event.ShipmentEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ShipmentEventPublisher {

    private final KafkaTemplate<String, ShipmentEvent> kafkaTemplate;

    public ShipmentEventPublisher(KafkaTemplate<String, ShipmentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(ShipmentEvent event) {
        kafkaTemplate.send(
                "shipment-events",
                event.getOrderId(), // key = orderId (important)
                event
        );
    }
}
