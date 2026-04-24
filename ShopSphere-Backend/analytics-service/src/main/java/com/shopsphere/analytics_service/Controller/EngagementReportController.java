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
@RequestMapping("/api/analytics/reports")
public class EngagementReportController {

    @Autowired
    private EngagementReportService engagementReportService;

    // SECURED: Only Admins can view all reports
    @GetMapping
    public ResponseEntity<?> getAllReports(
            @RequestHeader(value = "X-User-Role", defaultValue = "UNKNOWN") String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Admins only.");
        }
        List<EngagementReportResponse> reports = engagementReportService.getAllReports();
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    // SECURED: Only Admins can view a specific report
    @GetMapping("/{reportId}")
    public ResponseEntity<?> getReportById(
            @PathVariable("reportId") Long reportId,
            @RequestHeader(value = "X-User-Role", defaultValue = "UNKNOWN") String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Admins only.");
        }
        EngagementReportResponse report = engagementReportService.getReportById(reportId);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    // SECURED: Only Admins can view customer specific reports
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getReportsByCustomerId(
            @PathVariable("customerId") Long customerId,
            @RequestHeader(value = "X-User-Role", defaultValue = "UNKNOWN") String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Admins only.");
        }
        List<EngagementReportResponse> reports = engagementReportService.getReportsByCustomerId(customerId);
        return new ResponseEntity<>(reports, HttpStatus.OK);
    }

    // SECURED: Only Admins can create reports
    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestBody EngagementReportRequest request,
            @RequestHeader(value = "X-User-Role", defaultValue = "UNKNOWN") String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Admins only.");
        }
        EngagementReportResponse report = engagementReportService.createReport(request);
        return new ResponseEntity<>(report, HttpStatus.CREATED);
    }

    // SECURED: Only Admins can update reports
    @PutMapping("/{reportId}")
    public ResponseEntity<?> updateReport(
            @PathVariable("reportId") Long reportId,
            @RequestBody EngagementReportRequest request,
            @RequestHeader(value = "X-User-Role", defaultValue = "UNKNOWN") String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Admins only.");
        }
        EngagementReportResponse report = engagementReportService.updateReport(reportId, request);
        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    // SECURED: Only Admins can delete reports
    @DeleteMapping("/{reportId}")
    public ResponseEntity<?> deleteReport(
            @PathVariable("reportId") Long reportId,
            @RequestHeader(value = "X-User-Role", defaultValue = "UNKNOWN") String role) {
        if (!"ROLE_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access Denied: Admins only.");
        }
        engagementReportService.deleteReport(reportId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}