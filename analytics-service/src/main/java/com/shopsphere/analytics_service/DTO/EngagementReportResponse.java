package com.shopsphere.analytics_service.DTO;

import com.shopsphere.analytics_service.Entity.BehaviorMetricsEntity;
import com.shopsphere.analytics_service.Entity.CampaignResponseEntity;

import java.time.LocalDateTime;

public class EngagementReportResponse {

    private Long reportId;
    private Long customerId;
    private BehaviorMetricsEntity behaviorMetrics;
    private CampaignResponseEntity campaignResponse;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public BehaviorMetricsEntity getBehaviorMetrics() { return behaviorMetrics; }
    public void setBehaviorMetrics(BehaviorMetricsEntity behaviorMetrics) { this.behaviorMetrics = behaviorMetrics; }

    public CampaignResponseEntity getCampaignResponse() { return campaignResponse; }
    public void setCampaignResponse(CampaignResponseEntity campaignResponse) { this.campaignResponse = campaignResponse; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}