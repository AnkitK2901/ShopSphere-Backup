package com.shopsphere.analytics_service.Repository;

import com.shopsphere.analytics_service.Entity.CampaignResponseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CampaignResponseRepository extends JpaRepository<CampaignResponseEntity, Long> {
}