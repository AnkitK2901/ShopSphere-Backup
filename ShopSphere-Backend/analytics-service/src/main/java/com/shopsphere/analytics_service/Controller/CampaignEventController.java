package com.shopsphere.analytics_service.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics/campaigns")
public class CampaignEventController {

    private static final Logger logger = LoggerFactory.getLogger(CampaignEventController.class);

    // SECURED: Tracks when a user leaves without checking out (LLD 4.5.1)
    // This is a Presentation-Safe Mock Endpoint that simulates the event trigger
    @PostMapping("/trigger-abandoned-cart/{customerId}")
    public ResponseEntity<String> triggerAbandonedCartCampaign(
            @PathVariable Long customerId,
            @RequestHeader(value = "X-User-Role", defaultValue = "UNKNOWN") String role) {
        
        // Extra safety: Only internal systems or Admins should trigger this
        if (!"ROLE_ADMIN".equals(role)) {
            logger.warn("Unauthorized attempt to trigger campaigns by role: {}", role);
            return ResponseEntity.status(403).body("Access Denied: Only Administrators can trigger system campaigns.");
        }

        logger.warn("🚨 ABANDONED CART DETECTED: Customer ID [{}] left items in their cart.", customerId);
        logger.info("📧 Triggering Automated Email Campaign: Sending 10% Discount Code to Customer ID [{}]", customerId);
        
        // In a future sprint, this would persist directly to BehaviorMetricsEntity
        return ResponseEntity.ok("Successfully logged abandoned cart event and triggered recovery campaign for Customer ID: " + customerId);
    }
}