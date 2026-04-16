package com.shopsphere.analytics_service.Entity;

import com.shopsphere.analytics_service.Enums.CampaignResponseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class EntityTests {

    // ── EngagementReportEntity lifecycle callbacks ─────────────────────────────

    @Test
    @DisplayName("@PrePersist sets createdAt and updatedAt")
    void prePersistSetsTimestamps() {
        EngagementReportEntity entity = new EngagementReportEntity();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();

        entity.onCreate();

        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
        assertThat(entity.getCreatedAt()).isEqualToIgnoringNanos(entity.getUpdatedAt());
    }

    @Test
    @DisplayName("@PreUpdate refreshes updatedAt without changing createdAt")
    void preUpdateRefreshesUpdatedAt() {
        EngagementReportEntity entity = new EngagementReportEntity();
        entity.onCreate();  // simulate initial persist
        LocalDateTime originalCreatedAt = entity.getCreatedAt();
        LocalDateTime originalUpdatedAt = entity.getUpdatedAt();

        // Small delay so timestamps differ
        entity.onUpdate();

        assertThat(entity.getCreatedAt()).isEqualTo(originalCreatedAt);
        assertThat(entity.getUpdatedAt()).isAfterOrEqualTo(originalUpdatedAt);
    }

    // ── EngagementReportEntity relationships ──────────────────────────────────

    @Test
    @DisplayName("EngagementReportEntity holds BehaviorMetrics and CampaignResponse")
    void entityRelationships() {
        BehaviorMetricsEntity metrics = new BehaviorMetricsEntity();
        metrics.setTotalOrders(10);
        metrics.setAverageOrderValue(99.9);

        CampaignResponseEntity campaign = new CampaignResponseEntity();
        campaign.setCampaignName("Black Friday");
        campaign.setResponseStatus(CampaignResponseStatus.CONVERTED);

        EngagementReportEntity entity = new EngagementReportEntity();
        entity.setCustomerId(42L);
        entity.setBehaviorMetrics(metrics);
        entity.setCampaignResponse(campaign);

        assertThat(entity.getCustomerId()).isEqualTo(42L);
        assertThat(entity.getBehaviorMetrics().getTotalOrders()).isEqualTo(10);
        assertThat(entity.getCampaignResponse().getCampaignName()).isEqualTo("Black Friday");
        assertThat(entity.getCampaignResponse().getResponseStatus())
                .isEqualTo(CampaignResponseStatus.CONVERTED);
    }

    // ── BehaviorMetricsEntity getters/setters ─────────────────────────────────

    @Test
    @DisplayName("BehaviorMetricsEntity stores all fields correctly")
    void behaviorMetricsFields() {
        BehaviorMetricsEntity m = new BehaviorMetricsEntity();
        m.setMetricsId(5L);
        m.setTotalOrders(20);
        m.setRepeatPurchaseCount(15);
        m.setAbandonedCartCount(3);
        m.setFavouriteProduct("Gaming Headset");
        m.setAverageOrderValue(175.50);

        assertThat(m.getMetricsId()).isEqualTo(5L);
        assertThat(m.getTotalOrders()).isEqualTo(20);
        assertThat(m.getRepeatPurchaseCount()).isEqualTo(15);
        assertThat(m.getAbandonedCartCount()).isEqualTo(3);
        assertThat(m.getFavouriteProduct()).isEqualTo("Gaming Headset");
        assertThat(m.getAverageOrderValue()).isEqualTo(175.50);
    }

    // ── CampaignResponseEntity getters/setters ────────────────────────────────

    @Test
    @DisplayName("CampaignResponseEntity stores all fields correctly")
    void campaignResponseFields() {
        CampaignResponseEntity c = new CampaignResponseEntity();
        c.setCampaignResponseId(7L);
        c.setCampaignName("New Year Promo");
        c.setAbandonedCartReminderSent(false);
        c.setLoyaltyPoints(250);
        c.setResponseStatus(CampaignResponseStatus.OPENED);

        assertThat(c.getCampaignResponseId()).isEqualTo(7L);
        assertThat(c.getCampaignName()).isEqualTo("New Year Promo");
        assertThat(c.isAbandonedCartReminderSent()).isFalse();
        assertThat(c.getLoyaltyPoints()).isEqualTo(250);
        assertThat(c.getResponseStatus()).isEqualTo(CampaignResponseStatus.OPENED);
    }

    // ── CampaignResponseStatus enum coverage ──────────────────────────────────

    @Test
    @DisplayName("CampaignResponseStatus has all 5 expected values")
    void campaignResponseStatusEnum() {
        CampaignResponseStatus[] values = CampaignResponseStatus.values();

        assertThat(values).hasSize(5);
        assertThat(values).containsExactly(
                CampaignResponseStatus.SENT,
                CampaignResponseStatus.OPENED,
                CampaignResponseStatus.CLICKED,
                CampaignResponseStatus.CONVERTED,
                CampaignResponseStatus.IGNORED
        );
    }

    @Test
    @DisplayName("CampaignResponseStatus valueOf round-trips correctly")
    void campaignResponseStatusValueOf() {
        assertThat(CampaignResponseStatus.valueOf("CONVERTED"))
                .isEqualTo(CampaignResponseStatus.CONVERTED);
    }
}
