package com.shopsphere.analytics_service.Entity;

import com.shopsphere.analytics_service.Enums.CampaignResponseStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "CampaignResponse")
public class CampaignResponseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long campaignResponseId;

    private String campaignName;
    private boolean abandonedCartReminderSent;
    private int loyaltyPoints;

    @Enumerated(EnumType.STRING)
    private CampaignResponseStatus responseStatus;

    public Long getCampaignResponseId() { return campaignResponseId; }
    public void setCampaignResponseId(Long id) { this.campaignResponseId = id; }

    public String getCampaignName() { return campaignName; }
    public void setCampaignName(String campaignName) { this.campaignName = campaignName; }

    public boolean isAbandonedCartReminderSent() { return abandonedCartReminderSent; }
    public void setAbandonedCartReminderSent(boolean sent) { this.abandonedCartReminderSent = sent; }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public CampaignResponseStatus getResponseStatus() { return responseStatus; }
    public void setResponseStatus(CampaignResponseStatus responseStatus) { this.responseStatus = responseStatus; }

    public CampaignResponseEntity() {}
}