package com.shopsphere.auth_service.model;

public class EngagementReport {
    private String reportID;
    private String customerID;
    private String behaviorMetrics;
    private String campaignResponse;

    public String getReportID() {
        return reportID;
    }

    public void setReportID(String reportID) {
        this.reportID = reportID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getBehaviorMetrics() {
        return behaviorMetrics;
    }

    public void setBehaviorMetrics(String behaviorMetrics) {
        this.behaviorMetrics = behaviorMetrics;
    }

    public String getCampaignResponse() {
        return campaignResponse;
    }

    public void setCampaignResponse(String campaignResponse) {
        this.campaignResponse = campaignResponse;
    }
}