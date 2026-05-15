package com.shopsphere.logistics.service;

import com.shopsphere.logistics.carrier.CarrierClient;
import com.shopsphere.logistics.carrier.MockDelhiveryClient;
import com.shopsphere.logistics.carrier.MockShiprocketClient;
import com.shopsphere.logistics.carrier.MockFedExClient;
import com.shopsphere.logistics.client.CatalogFeignClient;
import com.shopsphere.logistics.client.OrderFeignClient;
import com.shopsphere.logistics.entity.Shipment;
import com.shopsphere.logistics.entity.ShipmentStatus;
import com.shopsphere.logistics.exception.InvalidShipmentStatusException;
import com.shopsphere.logistics.exception.ShipmentAlreadyExistsException;
import com.shopsphere.logistics.exception.ShipmentNotFoundException;
import com.shopsphere.logistics.repository.ShipmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

@Service
public class ShipmentService {

    private final ShipmentRepository repository;
    private final MockDelhiveryClient delhiveryClient;
    private final MockShiprocketClient shiprocketClient;
    private final MockFedExClient fedexClient;
    private final OrderFeignClient orderFeignClient;
    private final CatalogFeignClient catalogFeignClient; 

    public ShipmentService(ShipmentRepository repository,
                           MockDelhiveryClient delhiveryClient,
                           MockShiprocketClient shiprocketClient,
                           MockFedExClient fedexClient,
                           OrderFeignClient orderFeignClient,
                           CatalogFeignClient catalogFeignClient) { 
        this.repository = repository;
        this.delhiveryClient = delhiveryClient;
        this.shiprocketClient = shiprocketClient;
        this.fedexClient = fedexClient;
        this.orderFeignClient = orderFeignClient;
        this.catalogFeignClient = catalogFeignClient;
    }

    public List<Shipment> getAllShipments() {
        return repository.findAll();
    }

    public Shipment createShipment(String orderId) {
        if (repository.findByOrderId(orderId).isPresent()) {
            throw new ShipmentAlreadyExistsException(
                    "Shipment already exists for orderId: " + orderId);
        }

        Shipment shipment = new Shipment();
        shipment.setShipmentId(UUID.randomUUID().toString());
        shipment.setOrderId(orderId);
        shipment.setStatus(ShipmentStatus.CREATED);
        shipment.setCarrier("Unassigned");
        shipment.setTrackingNumber(null);
        shipment.setTrackingUrl(null);
        shipment.setEstimatedDelivery(null);
        shipment.setCreatedAt(LocalDateTime.now());
        shipment.setUpdatedAt(LocalDateTime.now());

        return repository.save(shipment);
    }

    public Shipment getShipmentByOrderId(String orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException(
                        "Shipment not found for orderId: " + orderId));
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getEnrichedShipmentByOrderId(String orderId) {
        Shipment shipment = repository.findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found for orderId: " + orderId));

        Map<String, Object> response = new HashMap<>();
        response.put("shipment", shipment);

        try {
            Map<String, Object> orderDetails = orderFeignClient.getOrderById(Long.parseLong(orderId));
            Object itemsObj = orderDetails.get("items");
            List<Map<String, Object>> orderItems = (itemsObj instanceof List) ? (List<Map<String, Object>>) itemsObj : new ArrayList<>();

            for (Map<String, Object> item : orderItems) {
                try {
                    Long productId = Long.valueOf(item.get("productId").toString());
                    Map<String, Object> productData = catalogFeignClient.getProductById(productId);
                    item.put("name", productData.get("name"));
                    item.put("previewImage", productData.get("previewImage"));
                    item.put("description", productData.get("description"));
                } catch (Exception e) {
                    item.put("name", "Product ID: " + item.get("productId"));
                }
            }
            response.put("items", orderItems);
        } catch (Exception e) {
            response.put("items", new ArrayList<>());
        }

        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public Shipment updateShipmentStatusByOrderId(String orderId, String status, String carrierName) {
        String internalStatus = status.toUpperCase();
        
        if ("DISPATCHED".equals(internalStatus) || "SHIPPED".equals(internalStatus)) {
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

        if (newStatus == ShipmentStatus.IN_TRANSIT && carrierName != null) {
            CarrierClient carrierClient;
            if ("FedEx".equalsIgnoreCase(carrierName)) {
                carrierClient = fedexClient;
            } else if ("Delhivery".equalsIgnoreCase(carrierName)) {
                carrierClient = delhiveryClient;
            } else {
                carrierClient = shiprocketClient;
            }
            
            Shipment mockDetails = carrierClient.createShipment(orderId);
            shipment.setCarrier(mockDetails.getCarrier());
            shipment.setTrackingNumber(mockDetails.getTrackingNumber());
            shipment.setTrackingUrl(mockDetails.getTrackingUrl());
            shipment.setEstimatedDelivery(mockDetails.getEstimatedDelivery());
        }

        shipment.setStatus(newStatus);
        shipment.setUpdatedAt(LocalDateTime.now());
        Shipment updatedShipment = repository.save(shipment);

        // THE FIX: If sync completely fails, the Exception is thrown, and the database perfectly rolls back.
        if (newStatus != ShipmentStatus.CANCELLED) {
            syncWithOrderService(orderId, newStatus.name());
        }

        return updatedShipment;
    }

    @Scheduled(fixedRate = 60000) 
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
            
            try {
                syncWithOrderService(shipment.getOrderId(), nextStatus.name());
            } catch (Exception e) {
                System.err.println("Simulator sync failed for Order: " + shipment.getOrderId());
            }
        }
    }

    // THE FIX: A professional retry mechanism to protect data integrity against micro-outages
    private void syncWithOrderService(String orderId, String internalStatus) {
        String mappedStatus = internalStatus;
        if ("IN_TRANSIT".equals(internalStatus)) {
            mappedStatus = "SHIPPED";
        }
        
        int maxRetries = 3;
        for (int i = 1; i <= maxRetries; i++) {
            try {
                orderFeignClient.updateOrderStatus(Long.parseLong(orderId), Map.of("newStatus", mappedStatus));
                System.out.println("✅ Successfully synced " + mappedStatus + " status to Order Service for Order: " + orderId);
                return; // Success! Exit the method.
            } catch (Exception e) {
                if (i == maxRetries) {
                    // Final failure. Throwing this triggers the @Transactional rollback so DBs stay identical.
                    throw new IllegalStateException("CRITICAL: Order Service unreachable after " + maxRetries + " attempts. Rolling back packing action to maintain Data Integrity.", e);
                }
                System.err.println("⚠️ Sync failed for Order " + orderId + ", retrying... (" + i + "/" + maxRetries + ")");
                try {
                    Thread.sleep(1000); // Wait 1 second before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Scheduled(fixedRate = 300000)
    public void sweepForOrphanedOrders() {
        System.out.println("🧹 SWEEPER: Checking for ghost orders...");
        try {
            List<Map<String, Object>> confirmedOrders = orderFeignClient.getOrdersByStatus("CONFIRMED");
            for (Map<String, Object> order : confirmedOrders) {
                Long orderId = Long.valueOf(order.get("orderId").toString());
                boolean exists = repository.findByOrderId(String.valueOf(orderId)).isPresent();
                if (!exists) {
                    System.out.println("🚨 GHOST ORDER DETECTED: Order ID " + orderId + " has no shipment! Creating now...");
                    createShipment(String.valueOf(orderId));
                }
            }
        } catch (Exception e) {
            System.err.println("Sweeper failed this cycle, will try again in 5 minutes: " + e.getMessage());
        }
    }

    private boolean isReadyForNextStage(Shipment shipment) {
        LocalDateTime lastUpdated = shipment.getUpdatedAt() != null ? shipment.getUpdatedAt() : shipment.getCreatedAt();
        long minutesElapsed = Duration.between(lastUpdated, LocalDateTime.now()).toMinutes();

        return switch (shipment.getStatus()) {
            case OUT_FOR_DELIVERY -> minutesElapsed >= 1; 
            case IN_TRANSIT -> minutesElapsed >= 2;
            default -> false;
        };
    }
    
    private ShipmentStatus getNextStatus(ShipmentStatus current) {
        return switch (current) {
            case CREATED -> throw new IllegalStateException("Cannot auto-progress CREATED.");
            case PACKED -> throw new IllegalStateException("Cannot auto-progress PACKED."); 
            case IN_TRANSIT -> ShipmentStatus.OUT_FOR_DELIVERY;
            case OUT_FOR_DELIVERY -> ShipmentStatus.DELIVERED;
            default -> throw new IllegalStateException("Invalid state");
        };
    }
}