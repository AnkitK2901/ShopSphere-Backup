package com.shopsphere.analytics_service.Service.Impl;

import com.shopsphere.analytics_service.Client.AuthFeignClient;
import com.shopsphere.analytics_service.Client.CatalogFeignClient;
import com.shopsphere.analytics_service.Client.OrderFeignClient;
import com.shopsphere.analytics_service.Client.OrderFeignClient.OrderResponse;
import com.shopsphere.analytics_service.DTO.EngagementReportRequest;
import com.shopsphere.analytics_service.DTO.EngagementReportResponse;
import com.shopsphere.analytics_service.Entity.BehaviorMetricsEntity;
import com.shopsphere.analytics_service.Entity.CampaignResponseEntity;
import com.shopsphere.analytics_service.Entity.EngagementReportEntity;
import com.shopsphere.analytics_service.Exception.ResourceNotFoundException;
import com.shopsphere.analytics_service.Repository.EngagementReportRepository;
import com.shopsphere.analytics_service.Service.EngagementReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EngagementReportServiceImpl implements EngagementReportService {

    private final EngagementReportRepository engagementReportRepository;
    private final OrderFeignClient orderFeignClient;
    private final AuthFeignClient authFeignClient;
    private final CatalogFeignClient catalogFeignClient;

    // ── Read ──────────────────────────────────────────────────────────────────

    @Override
    public List<EngagementReportResponse> getAllReports() {
        log.info("Fetching all engagement reports from the database");
        return engagementReportRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EngagementReportResponse getReportById(Long reportId) {
        log.info("Fetching engagement report with ID: {}", reportId);
        EngagementReportEntity report = engagementReportRepository.findById(reportId)
                .orElseThrow(() -> {
                    log.error("Report not found with ID: {}", reportId);
                    return new ResourceNotFoundException("Report not found with id: " + reportId);
                });
        return mapToResponse(report);
    }

    @Override
    public List<EngagementReportResponse> getReportsByCustomerId(Long customerId) {
        log.info("Fetching engagement reports for Customer ID: {}", customerId);
        List<EngagementReportEntity> reports = engagementReportRepository.findByCustomerId(customerId);
        if (reports.isEmpty()) {
            log.warn("No reports found for Customer ID: {}", customerId);
            throw new ResourceNotFoundException("No reports found for customer id: " + customerId);
        }
        return reports.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    // ── Write ─────────────────────────────────────────────────────────────────

    @Override
    public EngagementReportResponse createReport(EngagementReportRequest request) {
        log.info("Creating new engagement report for Customer ID: {}", request.getCustomerId());
        
        // Validate customer exists via auth-service
        try {
            authFeignClient.getUserById(request.getCustomerId());
        } catch (Exception e) {
            log.error("Customer validation failed. Customer ID {} not found in Auth Service.", request.getCustomerId());
            throw new ResourceNotFoundException("Customer not found in Auth Service: " + request.getCustomerId());
        }

        log.info("Fetching order history for Customer ID: {} to build metrics", request.getCustomerId());
        List<OrderResponse> orders = orderFeignClient.getOrdersByCustomerId(request.getCustomerId());

        EngagementReportEntity report = new EngagementReportEntity();
        report.setCustomerId(request.getCustomerId());
        report.setBehaviorMetrics(buildBehaviorMetrics(request, orders));
        report.setCampaignResponse(buildCampaignResponse(request));

        EngagementReportEntity savedReport = engagementReportRepository.save(report);
        log.info("Successfully created engagement report with ID: {}", savedReport.getReportId());
        
        return mapToResponse(savedReport);
    }

    @Override
    public EngagementReportResponse updateReport(Long reportId, EngagementReportRequest request) {
        log.info("Updating engagement report ID: {}", reportId);
        EngagementReportEntity report = engagementReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));

        List<OrderResponse> orders = orderFeignClient.getOrdersByCustomerId(report.getCustomerId());

        report.setBehaviorMetrics(buildBehaviorMetrics(request, orders));
        report.setCampaignResponse(buildCampaignResponse(request));

        return mapToResponse(engagementReportRepository.save(report));
    }

    @Override
    public void deleteReport(Long reportId) {
        log.info("Deleting engagement report ID: {}", reportId);
        EngagementReportEntity report = engagementReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));
        engagementReportRepository.delete(report);
        log.info("Successfully deleted report ID: {}", reportId);
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private BehaviorMetricsEntity buildBehaviorMetrics(EngagementReportRequest request,
                                                       List<OrderResponse> orders) {
        log.debug("Building behavior metrics. Total orders found: {}", orders.size());
        BehaviorMetricsEntity metrics = new BehaviorMetricsEntity();

        int totalOrders = orders.size();
        metrics.setTotalOrders(totalOrders);
        metrics.setRepeatPurchaseCount(totalOrders > 1 ? totalOrders - 1 : 0);
        metrics.setAbandonedCartCount(request.getAbandonedCartCount());

        // Favourite product — find most frequently ordered productId, then resolve
        // its human-readable name from catalog-service
        orders.stream()
                .collect(Collectors.groupingBy(OrderResponse::productId, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(e -> {
                    if (request.getFavouriteProduct() != null) {
                        metrics.setFavouriteProduct(request.getFavouriteProduct());
                    } else {
                        try {
                            CatalogFeignClient.ProductResponse product =
                                    catalogFeignClient.getProductById(e.getKey());
                            metrics.setFavouriteProduct(
                                    product != null ? product.name() : "Product-" + e.getKey()
                            );
                        } catch (Exception ex) {
                            log.warn("Catalog service unreachable while resolving favorite product ID: {}. Degraded gracefully.", e.getKey());
                            metrics.setFavouriteProduct("Product-" + e.getKey());
                        }
                    }
                });

        // Average order value
        double avgValue = totalOrders > 0
                ? orders.stream()
                .mapToDouble(o -> o.totalOrderAmount() != null ? o.totalOrderAmount() : 0.0)
                .average()
                .orElse(0.0)
                : 0.0;
        metrics.setAverageOrderValue(avgValue);

        return metrics;
    }

    private CampaignResponseEntity buildCampaignResponse(EngagementReportRequest request) {
        CampaignResponseEntity campaign = new CampaignResponseEntity();
        campaign.setCampaignName(request.getCampaignName());
        campaign.setAbandonedCartReminderSent(request.isAbandonedCartReminderSent());
        campaign.setLoyaltyPoints(request.getLoyaltyPoints());
        campaign.setResponseStatus(request.getResponseStatus());
        return campaign;
    }

    private EngagementReportResponse mapToResponse(EngagementReportEntity entity) {
        EngagementReportResponse response = new EngagementReportResponse();
        response.setReportId(entity.getReportId());
        response.setCustomerId(entity.getCustomerId());
        response.setBehaviorMetrics(entity.getBehaviorMetrics());
        response.setCampaignResponse(entity.getCampaignResponse());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}