package com.shopsphere.logistics.service;

import com.shopsphere.logistics.carrier.CarrierClient;
import com.shopsphere.logistics.carrier.MockDelhiveryClient;
import com.shopsphere.logistics.carrier.MockShiprocketClient;
import com.shopsphere.logistics.entity.Shipment;
import com.shopsphere.logistics.entity.ShipmentStatus;
import com.shopsphere.logistics.exception.InvalidShipmentStatusException;
import com.shopsphere.logistics.exception.ShipmentAlreadyExistsException;
import com.shopsphere.logistics.exception.ShipmentNotFoundException;
import com.shopsphere.logistics.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentService {

    private final ShipmentRepository repository;
    private final MockDelhiveryClient delhiveryClient;
    private final MockShiprocketClient shiprocketClient;

    public List<Shipment> getAllShipments() {
        log.info("Fetching all shipments from the database");
        return repository.findAll();
    }

    @Transactional
    public Shipment createShipment(String orderId) {
        log.info("Attempting to create shipment for Order ID: {}", orderId);
        if (repository.findByOrderId(orderId).isPresent()) {
            log.error("Shipment already exists for Order ID: {}", orderId);
            throw new ShipmentAlreadyExistsException(
                    "Shipment already exists for orderId: " + orderId);
        }

        // Logic to assign carrier dynamically
        CarrierClient carrier = orderId.hashCode() % 2 == 0 ? delhiveryClient : shiprocketClient;
        
        Shipment shipment = carrier.createShipment(orderId);
        shipment.setShipmentId(UUID.randomUUID().toString());
        shipment.setOrderId(orderId);
        shipment.setStatus(ShipmentStatus.CREATED);

        Shipment savedShipment = repository.save(shipment);
        log.info("Shipment successfully created. Shipment ID: {}, Assigned Carrier: {}", savedShipment.getShipmentId(), savedShipment.getCarrier());

        return savedShipment;
    }

    public Shipment getShipmentByOrderId(String orderId) {
        log.info("Fetching shipment details for Order ID: {}", orderId);
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    log.error("Shipment not found for Order ID: {}", orderId);
                    return new ShipmentNotFoundException("Shipment not found for orderId: " + orderId);
                });
    }

    @Transactional
    public Shipment updateShipmentStatusByOrderId(String orderId, String status) {
        log.info("Manual request to update Shipment status for Order ID: {} to {}", orderId, status);
        ShipmentStatus newStatus;
        try {
            newStatus = ShipmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            log.error("Invalid status provided: {}", status);
            throw new InvalidShipmentStatusException("Invalid shipment status: " + status);
        }

        Shipment shipment = getShipmentByOrderId(orderId);
        ShipmentStatus currentStatus = shipment.getStatus();

        if (!currentStatus.canTransitionTo(newStatus)) {
            log.warn("Invalid transition attempt from {} to {}", currentStatus, newStatus);
            throw new InvalidShipmentStatusException(
                    "Invalid status transition from " + currentStatus + " to " + newStatus);
        }

        shipment.setStatus(newStatus);
        Shipment updatedShipment = repository.save(shipment);
        log.info("Shipment status updated successfully for Order ID: {}", orderId);

        return updatedShipment;
    }

    // ADDED: @Scheduled so this automatically moves shipments forward
    // Runs every 60 seconds (60000ms) to simulate real-world logistics
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void simulateShipmentProgress() {
        log.info("[Scheduler] Simulating active shipment progress...");
        List<Shipment> activeShipments = repository.findByStatusNot(ShipmentStatus.DELIVERED);

        if (activeShipments.isEmpty()) {
            log.info("[Scheduler] No active shipments to update.");
            return;
        }

        for (Shipment shipment : activeShipments) {
            if (!isReadyForNextStage(shipment)) {
                continue;
            }

            ShipmentStatus nextStatus = getNextStatus(shipment.getStatus());
            shipment.setStatus(nextStatus);
            repository.save(shipment);
            
            log.info("[Scheduler] Shipment for Order ID: {} has progressed to {}", shipment.getOrderId(), nextStatus);
        }
    }

    private boolean isReadyForNextStage(Shipment shipment) {
        LocalDateTime lastUpdated = shipment.getUpdatedAt();
        long minutesElapsed = Duration.between(lastUpdated, LocalDateTime.now()).toMinutes();

        return switch (shipment.getStatus()) {
            case CREATED -> minutesElapsed >= 1;
            case PICKED_UP -> minutesElapsed >= 2;
            case IN_TRANSIT -> minutesElapsed >= 5;
            case OUT_FOR_DELIVERY -> minutesElapsed >= 2;
            default -> false;
        };
    }

    private ShipmentStatus getNextStatus(ShipmentStatus current) {
        return switch (current) {
            case CREATED -> ShipmentStatus.PICKED_UP;
            case PICKED_UP -> ShipmentStatus.IN_TRANSIT;
            case IN_TRANSIT -> ShipmentStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> ShipmentStatus.DELIVERED;
            default -> throw new IllegalStateException("Shipment already delivered");
        };
    }
}