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
                           OrderFeignClient orderFeignClient) {
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

        shipment.setCreatedAt(LocalDateTime.now());
        shipment.setUpdatedAt(LocalDateTime.now());

        return repository.save(shipment);
    }

    public Shipment getShipmentByOrderId(String orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException(
                        "Shipment not found for orderId: " + orderId));
    }

    public Shipment updateShipmentStatusByOrderId(String orderId, String status) {
        String internalStatus = status.toUpperCase();
        
        // Dictionary Fix: Translate UI actions to Logistics states
        if ("PACKED".equals(internalStatus)) {
            internalStatus = "PICKED_UP";
        } else if ("DISPATCHED".equals(internalStatus) || "SHIPPED".equals(internalStatus)) {
            internalStatus = "IN_TRANSIT";
        }

        ShipmentStatus newStatus;
        try {
            newStatus = ShipmentStatus.valueOf(internalStatus);
        } catch (IllegalArgumentException ex) {
            throw new InvalidShipmentStatusException("Invalid shipment status: " + status);
        }

        Shipment shipment = repository.findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found for orderId: " + orderId));

        if (shipment.getStatus() == newStatus) {
            return shipment;
        }

        if (!shipment.getStatus().canTransitionTo(newStatus)) {
            throw new InvalidShipmentStatusException(
                    "Invalid status transition from " + shipment.getStatus() + " to " + newStatus);
        }

        shipment.setStatus(newStatus);
        shipment.setUpdatedAt(LocalDateTime.now());
        Shipment updatedShipment = repository.save(shipment);

        // Sync to Order Service
        syncWithOrderService(orderId, newStatus.name());

        return updatedShipment;
    }

    @Transactional
    public void simulateShipmentProgress() {
        List<Shipment> activeShipments = repository.findByStatusNot(ShipmentStatus.DELIVERED);

        for (Shipment shipment : activeShipments) {
            if (!isReadyForNextStage(shipment)) {
                continue;
            }

            ShipmentStatus nextStatus = getNextStatus(shipment.getStatus());
            shipment.setStatus(nextStatus);
            shipment.setUpdatedAt(LocalDateTime.now());
            repository.save(shipment);
            
            syncWithOrderService(shipment.getOrderId(), nextStatus.name());
        }
    }

    private void syncWithOrderService(String orderId, String internalStatus) {
        String mappedStatus = internalStatus;
        
        if ("PICKED_UP".equals(internalStatus)) {
            mappedStatus = "PACKED";
        } else if ("IN_TRANSIT".equals(internalStatus) || "OUT_FOR_DELIVERY".equals(internalStatus)) {
            mappedStatus = "SHIPPED";
        }

        try {
            orderFeignClient.updateOrderStatus(Long.parseLong(orderId), Map.of("newStatus", mappedStatus));
            System.out.println("✅ Successfully synced " + mappedStatus + " status to Order Service for Order: " + orderId);
        } catch (Exception e) {
            System.err.println("❌ Failed to sync status to Order Service: " + e.getMessage());
        }
    }

    private boolean isReadyForNextStage(Shipment shipment) {
        LocalDateTime lastUpdated = shipment.getUpdatedAt() != null ? shipment.getUpdatedAt() : shipment.getCreatedAt();
        long minutesElapsed = Duration.between(lastUpdated, LocalDateTime.now()).toMinutes();

        return switch (shipment.getStatus()) {
            case CREATED, PICKED_UP, OUT_FOR_DELIVERY -> minutesElapsed >= 1;
            case IN_TRANSIT -> minutesElapsed >= 2;
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