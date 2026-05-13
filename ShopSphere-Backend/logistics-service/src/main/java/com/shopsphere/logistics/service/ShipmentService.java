package com.shopsphere.logistics.service;

import com.shopsphere.logistics.carrier.CarrierClient;
import com.shopsphere.logistics.carrier.MockDelhiveryClient;
import com.shopsphere.logistics.carrier.MockShiprocketClient;
import com.shopsphere.logistics.client.OrderFeignClient;
import com.shopsphere.logistics.entity.Shipment;
import com.shopsphere.logistics.entity.ShipmentStatus;
import com.shopsphere.logistics.exception.InvalidShipmentStatusException;
import com.shopsphere.logistics.exception.ShipmentAlreadyExistsException;
import com.shopsphere.logistics.exception.ShipmentNotFoundException;
import com.shopsphere.logistics.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ShipmentService {

    private final ShipmentRepository repository;
    private final MockDelhiveryClient delhiveryClient;
    private final MockShiprocketClient shiprocketClient;
    private final OrderFeignClient orderFeignClient;

    public ShipmentService(ShipmentRepository repository,
                           MockDelhiveryClient delhiveryClient,
                           MockShiprocketClient shiprocketClient,
                           OrderFeignClient orderFeignClient
                           ) {
        this.repository = repository;
        this.delhiveryClient = delhiveryClient;
        this.shiprocketClient = shiprocketClient;
        this.orderFeignClient = orderFeignClient;
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

        // THE FIX 1: Explicitly set timestamps to prevent NullPointerExceptions
        shipment.setCreatedAt(LocalDateTime.now());
        shipment.setUpdatedAt(LocalDateTime.now());

        Shipment savedShipment = repository.save(shipment);


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
        
        // Ensure manual updates also refresh the timestamp
        shipment.setUpdatedAt(LocalDateTime.now());

        Shipment updatedShipment = repository.save(shipment);

        syncWithOrderService(orderId, status);

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

            // THE FIX 2: Update the timestamp so the next stage has a valid starting point
            shipment.setUpdatedAt(LocalDateTime.now());

            repository.save(shipment);
            syncWithOrderService(shipment.getOrderId(), nextStatus.name());
        }
    }

    private void syncWithOrderService(String orderId, String status) {
        // THE FIX: Only notify the Order Service if the shipment is finally DELIVERED.
        // The Order Service's Enum does not understand "IN_TRANSIT" or "PICKED_UP".
        if ("DELIVERED".equalsIgnoreCase(status)) {
            try {
                orderFeignClient.updateOrderStatus(Long.parseLong(orderId), Map.of("newStatus", status));
                System.out.println("✅ Successfully synced DELIVERED status to Order Service for Order: " + orderId);
            } catch (Exception e) {
                System.err.println("❌ Failed to sync DELIVERED status to Order Service for ID: " + orderId);
            }
        }
    }

    private boolean isReadyForNextStage(Shipment shipment) {
        LocalDateTime lastUpdated = shipment.getUpdatedAt();
        
        // THE FIX 3: Null Safety Fallback in case old DB records lack timestamps
        if (lastUpdated == null) {
            lastUpdated = shipment.getCreatedAt() != null ? shipment.getCreatedAt() : LocalDateTime.now();
        }

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

}