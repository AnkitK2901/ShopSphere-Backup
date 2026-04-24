package com.shopsphere.analytics_service.Service;

import com.shopsphere.analytics_service.DTO.EngagementReportRequest;
import com.shopsphere.analytics_service.DTO.EngagementReportResponse;

import java.util.List;

public interface EngagementReportService {

    List<EngagementReportResponse> getAllReports();

    EngagementReportResponse getReportById(Long reportId);

    List<EngagementReportResponse> getReportsByCustomerId(Long customerId);

    EngagementReportResponse createReport(EngagementReportRequest request);

    EngagementReportResponse updateReport(Long reportId, EngagementReportRequest request);

    void deleteReport(Long reportId);
}