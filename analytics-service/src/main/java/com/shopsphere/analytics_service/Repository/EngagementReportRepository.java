package com.shopsphere.analytics_service.Repository;

import com.shopsphere.analytics_service.Entity.EngagementReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EngagementReportRepository extends JpaRepository<EngagementReportEntity, Long> {
    List<EngagementReportEntity> findByCustomerId(Long customerId);
    Optional<EngagementReportEntity> findFirstByCustomerIdOrderByCreatedAtDesc(Long customerId);
}