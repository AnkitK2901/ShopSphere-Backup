package com.shopsphere.analytics_service.Repository;

import com.shopsphere.analytics_service.Entity.BehaviorMetricsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BehaviorMetricsRepository extends JpaRepository<BehaviorMetricsEntity, Long> {
}