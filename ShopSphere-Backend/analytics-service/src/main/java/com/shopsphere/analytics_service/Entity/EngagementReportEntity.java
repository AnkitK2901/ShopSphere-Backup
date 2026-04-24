package com.shopsphere.analytics_service.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "EngagementReport")
public class EngagementReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    private Long customerId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "metrics_id")
    private BehaviorMetricsEntity behaviorMetrics;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "campaign_response_id")
    private CampaignResponseEntity campaignResponse;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

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

    public EngagementReportEntity() {}
}