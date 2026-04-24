package com.shopsphere.analytics_service.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shopsphere.analytics_service.DTO.EngagementReportRequest;
import com.shopsphere.analytics_service.DTO.EngagementReportResponse;
import com.shopsphere.analytics_service.Entity.BehaviorMetricsEntity;
import com.shopsphere.analytics_service.Entity.CampaignResponseEntity;
import com.shopsphere.analytics_service.Enums.CampaignResponseStatus;
import com.shopsphere.analytics_service.Exception.GlobalExceptionHandler;
import com.shopsphere.analytics_service.Exception.ResourceNotFoundException;
import com.shopsphere.analytics_service.Service.EngagementReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EngagementReportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EngagementReportService engagementReportService;

    @InjectMocks
    private EngagementReportController controller;

    private ObjectMapper objectMapper;
    private EngagementReportResponse sampleResponse;
    private EngagementReportRequest sampleRequest;

    @BeforeEach
    void setUp() {
        // Register GlobalExceptionHandler so 404/400/500 mapping works in tests
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Build sample response
        BehaviorMetricsEntity metrics = new BehaviorMetricsEntity();
        metrics.setMetricsId(1L);
        metrics.setTotalOrders(5);
        metrics.setRepeatPurchaseCount(4);
        metrics.setAbandonedCartCount(1);
        metrics.setFavouriteProduct("Wireless Mouse");
        metrics.setAverageOrderValue(250.0);

        CampaignResponseEntity campaign = new CampaignResponseEntity();
        campaign.setCampaignResponseId(1L);
        campaign.setCampaignName("Summer Sale");
        campaign.setAbandonedCartReminderSent(true);
        campaign.setLoyaltyPoints(100);
        campaign.setResponseStatus(CampaignResponseStatus.CLICKED);

        sampleResponse = new EngagementReportResponse();
        sampleResponse.setReportId(1L);
        sampleResponse.setCustomerId(100L);
        sampleResponse.setBehaviorMetrics(metrics);
        sampleResponse.setCampaignResponse(campaign);
        sampleResponse.setCreatedAt(LocalDateTime.of(2025, 4, 1, 10, 0));
        sampleResponse.setUpdatedAt(LocalDateTime.of(2025, 4, 1, 10, 0));

        // Build sample request
        sampleRequest = new EngagementReportRequest();
        sampleRequest.setCustomerId(100L);
        sampleRequest.setAbandonedCartCount(1);
        sampleRequest.setFavouriteProduct("Wireless Mouse");
        sampleRequest.setCampaignName("Summer Sale");
        sampleRequest.setAbandonedCartReminderSent(true);
        sampleRequest.setLoyaltyPoints(100);
        sampleRequest.setResponseStatus(CampaignResponseStatus.CLICKED);
    }

    // ── GET /api/engagement-reports ───────────────────────────────────────────

    @Test
    @DisplayName("GET /api/engagement-reports → 200 with list of reports")
    void getAllReports_returnsOk() throws Exception {
        when(engagementReportService.getAllReports()).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/engagement-reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].reportId").value(1))
                .andExpect(jsonPath("$[0].customerId").value(100))
                .andExpect(jsonPath("$[0].behaviorMetrics.totalOrders").value(5))
                .andExpect(jsonPath("$[0].campaignResponse.campaignName").value("Summer Sale"));
    }

    @Test
    @DisplayName("GET /api/engagement-reports → 200 with empty list")
    void getAllReports_returnsEmptyList() throws Exception {
        when(engagementReportService.getAllReports()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/engagement-reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // ── GET /api/engagement-reports/{reportId} ────────────────────────────────

    @Test
    @DisplayName("GET /api/engagement-reports/1 → 200 with report")
    void getReportById_returnsOk() throws Exception {
        when(engagementReportService.getReportById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/engagement-reports/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportId").value(1))
                .andExpect(jsonPath("$.behaviorMetrics.favouriteProduct").value("Wireless Mouse"));
    }

    @Test
    @DisplayName("GET /api/engagement-reports/999 → 404 when not found")
    void getReportById_returnsNotFound() throws Exception {
        when(engagementReportService.getReportById(999L))
                .thenThrow(new ResourceNotFoundException("Report not found with id: 999"));

        mockMvc.perform(get("/api/engagement-reports/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Report not found with id: 999"));
    }

    // ── GET /api/engagement-reports/customer/{customerId} ─────────────────────

    @Test
    @DisplayName("GET /api/engagement-reports/customer/100 → 200 with reports")
    void getReportsByCustomerId_returnsOk() throws Exception {
        when(engagementReportService.getReportsByCustomerId(100L))
                .thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/engagement-reports/customer/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].customerId").value(100));
    }

    @Test
    @DisplayName("GET /api/engagement-reports/customer/999 → 404 when none found")
    void getReportsByCustomerId_returnsNotFound() throws Exception {
        when(engagementReportService.getReportsByCustomerId(999L))
                .thenThrow(new ResourceNotFoundException("No reports found for customer id: 999"));

        mockMvc.perform(get("/api/engagement-reports/customer/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No reports found for customer id: 999"));
    }

    // ── POST /api/engagement-reports ──────────────────────────────────────────

    @Test
    @DisplayName("POST /api/engagement-reports → 201 created")
    void createReport_returnsCreated() throws Exception {
        when(engagementReportService.createReport(any(EngagementReportRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(post("/api/engagement-reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reportId").value(1))
                .andExpect(jsonPath("$.customerId").value(100));
    }

    @Test
    @DisplayName("POST /api/engagement-reports → 404 when customer not found")
    void createReport_customerNotFound() throws Exception {
        when(engagementReportService.createReport(any(EngagementReportRequest.class)))
                .thenThrow(new ResourceNotFoundException("Customer not found in Auth Service: 999"));

        sampleRequest.setCustomerId(999L);

        mockMvc.perform(post("/api/engagement-reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found in Auth Service: 999"));
    }

    // ── PUT /api/engagement-reports/{reportId} ────────────────────────────────

    @Test
    @DisplayName("PUT /api/engagement-reports/1 → 200 updated")
    void updateReport_returnsOk() throws Exception {
        when(engagementReportService.updateReport(eq(1L), any(EngagementReportRequest.class)))
                .thenReturn(sampleResponse);

        mockMvc.perform(put("/api/engagement-reports/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportId").value(1));
    }

    @Test
    @DisplayName("PUT /api/engagement-reports/999 → 404 when not found")
    void updateReport_returnsNotFound() throws Exception {
        when(engagementReportService.updateReport(eq(999L), any(EngagementReportRequest.class)))
                .thenThrow(new ResourceNotFoundException("Report not found with id: 999"));

        mockMvc.perform(put("/api/engagement-reports/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleRequest)))
                .andExpect(status().isNotFound());
    }

    // ── DELETE /api/engagement-reports/{reportId} ─────────────────────────────

    @Test
    @DisplayName("DELETE /api/engagement-reports/1 → 200 deleted")
    void deleteReport_returnsOk() throws Exception {
        doNothing().when(engagementReportService).deleteReport(1L);

        mockMvc.perform(delete("/api/engagement-reports/1"))
                .andExpect(status().isOk());

        verify(engagementReportService).deleteReport(1L);
    }

    @Test
    @DisplayName("DELETE /api/engagement-reports/999 → 404 when not found")
    void deleteReport_returnsNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Report not found with id: 999"))
                .when(engagementReportService).deleteReport(999L);

        mockMvc.perform(delete("/api/engagement-reports/999"))
                .andExpect(status().isNotFound());
    }
}
