package com.ewomen.greenfuture.controller;

import com.ewomen.greenfuture.dto.LocationResponse;
import com.ewomen.greenfuture.dto.WasteMapPoint;
import com.ewomen.greenfuture.entity.WasteReport;
import com.ewomen.greenfuture.service.ChartAnalyticsService;
import com.ewomen.greenfuture.service.WasteReportService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class WasteReportController {

    private final ChartAnalyticsService chartAnalyticsService;
    private final WasteReportService wasteReportService;

    public WasteReportController(
            WasteReportService wasteReportService,
            ChartAnalyticsService chartAnalyticsService) {

        this.wasteReportService = wasteReportService;
        this.chartAnalyticsService = chartAnalyticsService;
    }

    // ================= SUBMIT REPORT (CITIZENS / USERS) =================
    @PreAuthorize("hasAuthority('can_submit_reports')")
    @PostMapping
    public ResponseEntity<WasteReport> createReport(
            @RequestBody WasteReport report,
            Authentication authentication) {

        WasteReport savedReport = wasteReportService.createReport(report, authentication);

        return ResponseEntity.ok(savedReport);
    }

    // ================= VIEW ALL REPORTS (ADMIN / ANALYTICS) =================
    @PreAuthorize("hasAuthority('can_view_reports')")
    @GetMapping
    public List<WasteReport> getAllReports() {
        return this.wasteReportService.getAllReports();
    }

    // ================= REPORT LOCATIONS (DASHBOARD / ANALYTICS) =================
    @PreAuthorize("hasAuthority('can_view_dashboard')")
    @GetMapping("/locations")
    public List<LocationResponse> getLocations() {
        return wasteReportService.getReportLocations();
    }

    @GetMapping("/map-points")
    public List<WasteMapPoint> getMapPoints() {

        return chartAnalyticsService.getMapPoints();
    }
}
