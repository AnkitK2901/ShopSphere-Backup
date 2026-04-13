package com.shopsphere.logistics.repository;

import com.shopsphere.logistics.entity.Shipment;

import java.util.List;
import java.util.Optional;

import com.shopsphere.logistics.entity.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ShipmentRepository extends JpaRepository<Shipment, String> {
    Optional<Shipment> findByOrderId(String orderId);
    List<Shipment> findByStatusNot(ShipmentStatus status);

}