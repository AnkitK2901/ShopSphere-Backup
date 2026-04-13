package com.shopsphere.analytics_service.Controller;

import com.shopsphere.analytics_service.DTO.EngagementReportRequest;
import com.shopsphere.analytics_service.DTO.EngagementReportResponse;
import com.shopsphere.analytics_service.Service.EngagementReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/engagement-reports")
public class EngagementReportController {

    @Autowired
    private EngagementReportService engagementReportService;

    @GetMapping
    public ResponseEntity<List<EngagementReportResponse>> getAllReports() {
        List<EngagementReportResponse> reports = engagementReportService.getAllReports();
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<EngagementReportResponse> getReportById(@PathVariable("reportId") Long reportId) {
        EngagementReportResponse report = engagementReportService.getReportById(reportId);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<EngagementReportResponse>> getReportsByCustomerId(@PathVariable("customerId") Long customerId) {
        List<EngagementReportResponse> reports = engagementReportService.getReportsByCustomerId(customerId);
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<EngagementReportResponse> createReport(@RequestBody EngagementReportRequest request) {
        EngagementReportResponse report = engagementReportService.createReport(request);
        return new ResponseEntity<>(report, HttpStatus.CREATED);
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<EngagementReportResponse> updateReport(
            @PathVariable("reportId") Long reportId,
            @RequestBody EngagementReportRequest request) {
        EngagementReportResponse report = engagementReportService.updateReport(reportId, request);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    @DeleteMapping("/{reportId}")
    public ResponseEntity<Void> deleteReport(@PathVariable("reportId") Long reportId) {
        engagementReportService.deleteReport(reportId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}