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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
 
@Service
public class EngagementReportServiceImpl implements EngagementReportService {
 
    @Autowired
    private EngagementReportRepository engagementReportRepository;
 
    @Autowired
    private OrderFeignClient orderFeignClient;
 
    @Autowired
    private AuthFeignClient authFeignClient;
 
    @Autowired
    private CatalogFeignClient catalogFeignClient;
 
    // ── Read ──
 
    @Override
    public List<EngagementReportResponse> getAllReports() {
        return engagementReportRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
 
    @Override
    public EngagementReportResponse getReportById(Long reportId) {
        EngagementReportEntity report = findReportOrThrow(reportId);
        return mapToResponse(report);
    }
 
    @Override
    public List<EngagementReportResponse> getReportsByCustomerId(Long customerId) {
        List<EngagementReportEntity> reports = engagementReportRepository.findByCustomerId(customerId);
        if (reports.isEmpty()) {
            throw new ResourceNotFoundException("No reports found for customer id: " + customerId);
        }
        return reports.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
 
    // ── Write ──
 
    @Override
    public EngagementReportResponse createReport(EngagementReportRequest request) {
        // If auth-service returns 404 → FeignException.NotFound is thrown
        // GlobalExceptionHandler catches it and returns a proper 404 response
        authFeignClient.getUserById(request.getCustomerId());
 
        List<OrderResponse> orders = orderFeignClient.getOrdersByCustomerId(request.getCustomerId());
 
        EngagementReportEntity report = new EngagementReportEntity();
        report.setCustomerId(request.getCustomerId());
        report.setBehaviorMetrics(buildBehaviorMetrics(request, orders));
        report.setCampaignResponse(buildCampaignResponse(request));
 
        return mapToResponse(engagementReportRepository.save(report));
    }
 
    @Override
    public EngagementReportResponse updateReport(Long reportId, EngagementReportRequest request) {
        EngagementReportEntity report = findReportOrThrow(reportId);
 
        List<OrderResponse> orders = orderFeignClient.getOrdersByCustomerId(report.getCustomerId());
 
        report.setBehaviorMetrics(buildBehaviorMetrics(request, orders));
        report.setCampaignResponse(buildCampaignResponse(request));
 
        return mapToResponse(engagementReportRepository.save(report));
    }
 
    @Override
    public void deleteReport(Long reportId) {
        EngagementReportEntity report = findReportOrThrow(reportId);
        engagementReportRepository.delete(report);
    }
 
    // ── Private Helpers ──
 
    private EngagementReportEntity findReportOrThrow(Long reportId) {
        return engagementReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));
    }
 
    // ── Build BehaviorMetrics ──
 
    private BehaviorMetricsEntity buildBehaviorMetrics(EngagementReportRequest request,
                                                       List<OrderResponse> orders) {
        BehaviorMetricsEntity metrics = new BehaviorMetricsEntity();
 
        int totalOrders = orders.size();
 
        metrics.setTotalOrders(totalOrders);
        metrics.setRepeatPurchaseCount(totalOrders > 1 ? totalOrders - 1 : 0);
        metrics.setAbandonedCartCount(request.getAbandonedCartCount());
        metrics.setFavouriteProduct(resolveFavouriteProduct(request, orders));
        metrics.setAverageOrderValue(calculateAverageOrderValue(orders));
 
        return metrics;
    }
 
    /**
     * If the request already has a favourite product name, use it.
     * Otherwise, find the most frequently ordered productId
     * and look up its name from catalog-service.
     */
    private String resolveFavouriteProduct(EngagementReportRequest request,
                                           List<OrderResponse> orders) {
        if (request.getFavouriteProduct() != null) {
            return request.getFavouriteProduct();
        }
 
        String topProductId = findMostOrderedProductId(orders);
        if (topProductId == null) {
            return null;
        }
 
        return fetchProductName(topProductId);
    }
 
    /**
     * Groups orders by productId and returns the one with the highest count.
     * Returns null if the order list is empty.
     */
    private String findMostOrderedProductId(List<OrderResponse> orders) {
        return orders.stream()
                .collect(Collectors.groupingBy(OrderResponse::productId, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
 
    /**
     * Calls catalog-service to get the product name.
     * Falls back to "Product-{id}" if the call fails.
     *
     * WHY TRY-CATCH HERE:
     * This is intentional graceful degradation. If catalog-service is down,
     * we still want the report to be created — just with a fallback product name.
     * The GlobalExceptionHandler cannot do this because it can only return
     * error responses to the client. It cannot return a fallback value
     * and let the business logic continue.
     */
    private String fetchProductName(String productId) {
        try {
            CatalogFeignClient.ProductResponse product = catalogFeignClient.getProductById(productId);
            return product != null ? product.name() : "Product-" + productId;
        } catch (Exception ex) {
            return "Product-" + productId;
        }
    }
 
    /**
     * Calculates the average totalOrderAmount, rounded to 2 decimal places.
     * Returns 0.0 if there are no orders.
     */
    private double calculateAverageOrderValue(List<OrderResponse> orders) {
        if (orders.isEmpty()) {
            return 0.0;
        }
        double average = orders.stream()
                .mapToDouble(o -> o.totalOrderAmount() != null ? o.totalOrderAmount() : 0.0)
                .average()
                .orElse(0.0);
 
        return Math.round(average * 100.0) / 100.0;
    }
 
    // ── Build CampaignResponse ──
 
    private CampaignResponseEntity buildCampaignResponse(EngagementReportRequest request) {
        CampaignResponseEntity campaign = new CampaignResponseEntity();
        campaign.setCampaignName(request.getCampaignName());
        campaign.setAbandonedCartReminderSent(request.isAbandonedCartReminderSent());
        campaign.setLoyaltyPoints(request.getLoyaltyPoints());
        campaign.setResponseStatus(request.getResponseStatus());
        return campaign;
    }
 
    // ── Entity → DTO ──
 
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