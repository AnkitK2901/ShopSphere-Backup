package com.shopsphere.analytics_service.DTO;

import com.shopsphere.analytics_service.Enums.CampaignResponseStatus;

public class EngagementReportRequest {

    private Long customerId;
    private int abandonedCartCount;
    private String favouriteProduct;
    private String campaignName;
    private boolean abandonedCartReminderSent;
    private int loyaltyPoints;
    private CampaignResponseStatus responseStatus;

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public int getAbandonedCartCount() { return abandonedCartCount; }
    public void setAbandonedCartCount(int abandonedCartCount) { this.abandonedCartCount = abandonedCartCount; }

    public String getFavouriteProduct() { return favouriteProduct; }
    public void setFavouriteProduct(String favouriteProduct) { this.favouriteProduct = favouriteProduct; }

    public String getCampaignName() { return campaignName; }
    public void setCampaignName(String campaignName) { this.campaignName = campaignName; }

    public boolean isAbandonedCartReminderSent() { return abandonedCartReminderSent; }
    public void setAbandonedCartReminderSent(boolean sent) { this.abandonedCartReminderSent = sent; }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public CampaignResponseStatus getResponseStatus() { return responseStatus; }
    public void setResponseStatus(CampaignResponseStatus responseStatus) { this.responseStatus = responseStatus; }
}