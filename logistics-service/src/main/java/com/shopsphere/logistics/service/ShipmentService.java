package com.shopsphere.logistics.service;

import com.shopsphere.logistics.carrier.CarrierClient;
import com.shopsphere.logistics.carrier.MockDelhiveryClient;
import com.shopsphere.logistics.carrier.MockShiprocketClient;
import com.shopsphere.logistics.entity.Shipment;
import com.shopsphere.logistics.entity.ShipmentStatus;
//import com.shopsphere.logistics.event.ShipmentEvent;
import com.shopsphere.logistics.exception.InvalidShipmentStatusException;
import com.shopsphere.logistics.exception.ShipmentAlreadyExistsException;
import com.shopsphere.logistics.exception.ShipmentNotFoundException;
//import com.shopsphere.logistics.kafka.ShipmentEventPublisher;
import com.shopsphere.logistics.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ShipmentService {

    private final ShipmentRepository repository;
    private final MockDelhiveryClient delhiveryClient;
    private final MockShiprocketClient shiprocketClient;
    //private final ShipmentEventPublisher eventPublisher;

    public ShipmentService(ShipmentRepository repository,
                           MockDelhiveryClient delhiveryClient,
                           MockShiprocketClient shiprocketClient
                           /*,ShipmentEventPublisher eventPublisher*/) {
        this.repository = repository;
        this.delhiveryClient = delhiveryClient;
        this.shiprocketClient = shiprocketClient;
        //this.eventPublisher = eventPublisher;
    }

    public List<Shipment> getAllShipments() {
        return repository.findAll();
    }

    public Shipment createShipment(String orderId) {
        if (repository.findByOrderId(orderId).isPresent()) {
            throw new ShipmentAlreadyExistsException(
                    "Shipment already exists for orderId: " + orderId);
        }

        CarrierClient carrier =
                orderId.hashCode() % 2 == 0 ? delhiveryClient : shiprocketClient;

        Shipment shipment = carrier.createShipment(orderId);
        shipment.setShipmentId(UUID.randomUUID().toString());
        shipment.setOrderId(orderId);
        shipment.setStatus(ShipmentStatus.CREATED);

        Shipment savedShipment = repository.save(shipment);

        //eventPublisher.publish(toEvent(savedShipment, "CREATED"));

        return savedShipment;
    }


    public Shipment getShipmentByOrderId(String orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException(
                        "Shipment not found for orderId: " + orderId));
    }

    public Shipment updateShipmentStatusByOrderId(String orderId, String status) {

        ShipmentStatus newStatus;
        try {
            newStatus = ShipmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidShipmentStatusException(
                    "Invalid shipment status: " + status);
        }

        Shipment shipment = repository.findByOrderId(orderId)
                .orElseThrow(() ->
                        new ShipmentNotFoundException(
                                "Shipment not found for orderId: " + orderId));

        ShipmentStatus currentStatus = shipment.getStatus();

        if (!currentStatus.canTransitionTo(newStatus)) {
            throw new InvalidShipmentStatusException(
                    "Invalid status transition from "
                            + currentStatus + " to " + newStatus);
        }

        shipment.setStatus(newStatus);

        Shipment updatedShipment = repository.save(shipment);

        //eventPublisher.publish(toEvent(updatedShipment, "STATUS_UPDATED"));

        return updatedShipment;
    }

    @Transactional
    public void simulateShipmentProgress() {
        List<Shipment> activeShipments =
                repository.findByStatusNot(ShipmentStatus.DELIVERED);

        for (Shipment shipment : activeShipments) {

            if (!isReadyForNextStage(shipment)) {
                continue;
            }

            ShipmentStatus nextStatus = getNextStatus(shipment.getStatus());
            shipment.setStatus(nextStatus);

            repository.save(shipment);

            //eventPublisher.publish(toEvent(shipment, "STATUS_UPDATED"));

        }
    }
    private boolean isReadyForNextStage(Shipment shipment) {
        LocalDateTime lastUpdated = shipment.getUpdatedAt();
        long minutesElapsed =
                Duration.between(lastUpdated, LocalDateTime.now()).toMinutes();

        boolean result;

        switch (shipment.getStatus()) {
            case CREATED:
                result = minutesElapsed >= 1;
                break;
            case PICKED_UP:
                result = minutesElapsed >= 2;
                break;
            case IN_TRANSIT:
                result = minutesElapsed >= 5;
                break;
            case OUT_FOR_DELIVERY:
                result = minutesElapsed >= 2;
                break;
            default:
                result = false;
                break;
        }

        return result;
    }
    private ShipmentStatus getNextStatus(ShipmentStatus current) {
        ShipmentStatus nextStatus;

        switch (current) {
            case CREATED:
                nextStatus = ShipmentStatus.PICKED_UP;
                break;
            case PICKED_UP:
                nextStatus = ShipmentStatus.IN_TRANSIT;
                break;
            case IN_TRANSIT:
                nextStatus = ShipmentStatus.OUT_FOR_DELIVERY;
                break;
            case OUT_FOR_DELIVERY:
                nextStatus = ShipmentStatus.DELIVERED;
                break;
            default:
                throw new IllegalStateException("Shipment already delivered");
        }

        return nextStatus;
    }
//    private ShipmentEvent toEvent(Shipment shipment, String eventType) {
//        ShipmentEvent event = new ShipmentEvent();
//        event.setOrderId(shipment.getOrderId());
//        event.setShipmentId(shipment.getShipmentId());
//        event.setTrackingNumber(shipment.getTrackingNumber());
//        event.setStatus(shipment.getStatus());
//        event.setEventType(eventType);
//        event.setEventTime(LocalDateTime.now());
//        return event;
//    }
}

