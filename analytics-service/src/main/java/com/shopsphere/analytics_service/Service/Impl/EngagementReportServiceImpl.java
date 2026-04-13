package com.shopsphere.analytics_service.Service.Impl;

import com.shopsphere.analytics_service.Client.OrderServiceClient;
import com.shopsphere.analytics_service.Client.OrderServiceClient.OrderResponse;
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
    private OrderServiceClient orderServiceClient;

    @Override
    public List<EngagementReportResponse> getAllReports() {
        return engagementReportRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EngagementReportResponse getReportById(Long reportId) {
        EngagementReportEntity report = engagementReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));
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

    @Override
    public EngagementReportResponse createReport(EngagementReportRequest request) {
        if (!orderServiceClient.customerExists(request.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found in Order Service: " + request.getCustomerId());
        }

        List<OrderResponse> orders = orderServiceClient.getOrdersByCustomerId(request.getCustomerId());

        EngagementReportEntity report = new EngagementReportEntity();
        report.setCustomerId(request.getCustomerId());
        report.setBehaviorMetrics(buildBehaviorMetrics(request, orders));
        report.setCampaignResponse(buildCampaignResponse(request));

        return mapToResponse(engagementReportRepository.save(report));
    }

    @Override
    public EngagementReportResponse updateReport(Long reportId, EngagementReportRequest request) {
        EngagementReportEntity report = engagementReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));

        List<OrderResponse> orders = orderServiceClient.getOrdersByCustomerId(report.getCustomerId());

        report.setBehaviorMetrics(buildBehaviorMetrics(request, orders));
        report.setCampaignResponse(buildCampaignResponse(request));

        return mapToResponse(engagementReportRepository.save(report));
    }

    @Override
    public void deleteReport(Long reportId) {
        EngagementReportEntity report = engagementReportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + reportId));
        engagementReportRepository.delete(report);
    }

    // ----------- Private Helpers -----------

    private BehaviorMetricsEntity buildBehaviorMetrics(EngagementReportRequest request, List<OrderResponse> orders) {
        BehaviorMetricsEntity metrics = new BehaviorMetricsEntity();

        int totalOrders = orders.size();
        metrics.setTotalOrders(totalOrders);
        metrics.setRepeatPurchaseCount(totalOrders > 1 ? totalOrders - 1 : 0);
        metrics.setAbandonedCartCount(request.getAbandonedCartCount());

        orders.stream()
                .collect(Collectors.groupingBy(OrderResponse::productId, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(e -> metrics.setFavouriteProduct(
                        request.getFavouriteProduct() != null
                                ? request.getFavouriteProduct()
                                : "Product-" + e.getKey()
                ));

        if (totalOrders > 0) {
            double totalValue = orders.stream()
                    .mapToDouble(order -> {
                        OrderServiceClient.ProductResponse product =
                                orderServiceClient.getProductById(order.productId());
                        return product != null ? product.basePrice() : 0.0;
                    })
                    .sum();
            metrics.setAverageOrderValue(totalValue / totalOrders);
        } else {
            metrics.setAverageOrderValue(0.0);
        }

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