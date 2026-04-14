package com.shopsphere.analytics_service.Controller;

import com.shopsphere.analytics_service.DTO.EngagementReportRequest;
import com.shopsphere.analytics_service.DTO.EngagementReportResponse;
import com.shopsphere.analytics_service.Service.EngagementReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/engagement-reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics & Customer Engagement", description = "APIs for tracking customer behavior, loyalty, and abandoned carts")
public class EngagementReportController {

    private final EngagementReportService engagementReportService;

    @Operation(summary = "Get all engagement reports")
    @GetMapping
    public ResponseEntity<List<EngagementReportResponse>> getAllReports() {
        log.info("REST request to fetch all engagement reports");
        List<EngagementReportResponse> reports = engagementReportService.getAllReports();
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    @Operation(summary = "Get a specific engagement report by ID")
    @GetMapping("/{reportId}")
    public ResponseEntity<EngagementReportResponse> getReportById(@PathVariable("reportId") Long reportId) {
        log.info("REST request to fetch report ID: {}", reportId);
        EngagementReportResponse report = engagementReportService.getReportById(reportId);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @Operation(summary = "Get all engagement reports for a specific Customer ID")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<EngagementReportResponse>> getReportsByCustomerId(@PathVariable("customerId") Long customerId) {
        log.info("REST request to fetch reports for Customer ID: {}", customerId);
        List<EngagementReportResponse> reports = engagementReportService.getReportsByCustomerId(customerId);
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    @Operation(summary = "Generate a new engagement report")
    @PostMapping
    public ResponseEntity<EngagementReportResponse> createReport(@RequestBody EngagementReportRequest request) {
        log.info("REST request to create an engagement report");
        EngagementReportResponse report = engagementReportService.createReport(request);
        return new ResponseEntity<>(report, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing engagement report")
    @PutMapping("/{reportId}")
    public ResponseEntity<EngagementReportResponse> updateReport(
            @PathVariable("reportId") Long reportId,
            @RequestBody EngagementReportRequest request) {
        log.info("REST request to update report ID: {}", reportId);
        EngagementReportResponse report = engagementReportService.updateReport(reportId, request);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @Operation(summary = "Delete an engagement report")
    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable("reportId") Long reportId) {
        log.info("REST request to delete report ID: {}", reportId);
        engagementReportService.deleteReport(reportId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}