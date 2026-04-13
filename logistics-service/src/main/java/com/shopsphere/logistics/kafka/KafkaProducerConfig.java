package com.shopsphere.logistics.kafka;

import com.shopsphere.logistics.event.ShipmentEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, ShipmentEvent> kafkaTemplate(
            ProducerFactory<String, ShipmentEvent> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
