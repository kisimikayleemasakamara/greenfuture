package com.ewomen.greenfuture.service;

import com.ewomen.greenfuture.dto.CommunityLeaderboardResponse;
import com.ewomen.greenfuture.dto.PublicDashboardStatsResponse;
import com.ewomen.greenfuture.dto.PublicWasteReportRequest;
import com.ewomen.greenfuture.entity.ReportStatus;
import com.ewomen.greenfuture.entity.WasteReport;
import com.ewomen.greenfuture.repository.CommunityRepository;
import com.ewomen.greenfuture.repository.EcoTrikeRepository;
import com.ewomen.greenfuture.repository.WasteReportRepository;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class PublicService {

    private final WasteReportRepository wasteReportRepository;
    private final CommunityRepository communityRepository;
    private final EcoTrikeRepository ecoTrikeRepository;
    private final AnalyticsService analyticsService;
    private final FileStorageService fileStorageService;

    // Constructor Injection
    public PublicService(
            WasteReportRepository wasteReportRepository,
            CommunityRepository communityRepository,
            EcoTrikeRepository ecoTrikeRepository,
            AnalyticsService analyticsService,
            FileStorageService fileStorageService) {

        this.wasteReportRepository = wasteReportRepository;
        this.communityRepository = communityRepository;
        this.ecoTrikeRepository = ecoTrikeRepository;
        this.analyticsService = analyticsService;
        this.fileStorageService = fileStorageService;
    }

    public PublicDashboardStatsResponse getDashboardStats() {

        long totalReports = wasteReportRepository.count();
        long totalUsers = communityRepository.count();
        long totalResolved = 0;
        long totalPending = 0;
        long totalPoints = ecoTrikeRepository.count();

        return new PublicDashboardStatsResponse(
                totalReports,
                totalUsers,
                totalResolved,
                totalPending,
                totalPoints);
    }

    public List<CommunityLeaderboardResponse> getCommunityLeaderboard() {

        return analyticsService.getCommunityLeaderboard();
    }

    public void submitPublicReport(
            PublicWasteReportRequest request) {

        String imageUrl = fileStorageService
                .saveFile(
                        request.getImage());

        WasteReport report = new WasteReport();

        report.setDescription(
                request.getDescription());

        report.setLatitude(
                request.getLatitude());

        report.setLongitude(
                request.getLongitude());

        report.setImageUrl(
                imageUrl);

        report.setStatus(
                ReportStatus.PENDING);

        report.setPublicSubmission(
                true);

        report.setPublicReporterName(
                request.getReporterName());

        report.setPublicPhoneNumber(
                request.getPhoneNumber());

        wasteReportRepository.save(
                report);
    }
}

/**
 * package com.ewomen.greenfuture.service;
 * 
 * import lombok.RequiredArgsConstructor;
 * import org.springframework.stereotype.Service;
 * 
 * import com.ewomen.greenfuture.dto.PublicDashboardStatsResponse;
 * import com.ewomen.greenfuture.entity.ReportStatus;
 * import com.ewomen.greenfuture.repository.CommunityRepository;
 * import com.ewomen.greenfuture.repository.EcoTrikeRepository;
 * import com.ewomen.greenfuture.repository.WasteReportRepository;
 * 
 * @Service
 * @RequiredArgsConstructor
 *                          public class PublicService {
 *                          private final WasteReportRepository
 *                          wasteReportRepository;
 *                          private final CommunityRepository
 *                          communityRepository;
 *                          private final EcoTrikeRepository ecoTrikeRepository;
 * 
 *                          public PublicDashboardStatsResponse
 *                          getDashboardStats() {
 * 
 *                          long totalReports = wasteReportRepository.count();
 * 
 *                          long resolvedReports = wasteReportRepository
 *                          .countByStatus(
 *                          ReportStatus.COMPLETED);
 * 
 *                          long pendingReports = wasteReportRepository
 *                          .countByStatus(
 *                          ReportStatus.PENDING);
 * 
 *                          long totalCommunities = communityRepository.count();
 * 
 *                          long totalEcoTrikes = ecoTrikeRepository.count();
 * 
 *                          return new PublicDashboardStatsResponse(
 *                          totalReports,
 *                          resolvedReports,
 *                          pendingReports,
 *                          totalCommunities,
 *                          totalEcoTrikes);
 *                          }
 *                          }
 **/