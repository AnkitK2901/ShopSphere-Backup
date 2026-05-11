package com.shopsphere.order.DTO;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "analytics-service", path = "/api/analytics")
public interface AnalyticsClient {
    
    // We send the amount directly to the Analytics Controller
    @PostMapping("/revenue/log")
    void logRevenueEvent(@RequestParam("amount") Double amount);
}