package com.ewomen.greenfuture.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.ewomen.greenfuture.dto.LocationResponse;
import com.ewomen.greenfuture.dto.WasteMapPoint;
import com.ewomen.greenfuture.entity.User;
import com.ewomen.greenfuture.entity.WasteReport;
import com.ewomen.greenfuture.repository.WasteReportRepository;

@Service
public class WasteReportService {

    private final WasteReportRepository wasteReportRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    public WasteReportService(
            WasteReportRepository wasteReportRepository,
            UserService userService,
            NotificationService notificationService) {

        this.wasteReportRepository = wasteReportRepository;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    /**
     * Create a new waste report
     */
    public WasteReport createReport(WasteReport report, Authentication authentication) {

        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("User is not authenticated");
        }

        String email = authentication.getName();

        User user = userService.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Assign ownership
        report.setUser(user);

        // Save report
        WasteReport savedReport = wasteReportRepository.save(report);

        // Create notification
        notificationService.createNotification(
                "Waste Report Submitted",
                "Your waste report has been received successfully.",
                user);

        return savedReport;
    }

    /**
     * Get all reports
     */
    public List<WasteReport> getAllReports() {
        return wasteReportRepository.findAll();
    }

    /**
     * Get report locations for map display
     */
    public List<LocationResponse> getReportLocations() {

        return wasteReportRepository.findAll()
                .stream()
                .map(report -> new LocationResponse(
                        report.getTitle(),
                        report.getLatitude(),
                        report.getLongitude()))
                .toList();
    }

    public List<WasteMapPoint> getMapPoints() {

        return wasteReportRepository.getWasteMapPoints();
    }
}