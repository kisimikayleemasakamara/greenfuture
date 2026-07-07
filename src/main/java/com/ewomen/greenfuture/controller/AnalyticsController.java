package com.ewomen.greenfuture.controller;

import com.ewomen.greenfuture.dto.AnalyticsResponse;
import com.ewomen.greenfuture.dto.ChartDataProjection;
import com.ewomen.greenfuture.dto.ChartDataResponse;
import com.ewomen.greenfuture.dto.CommunityLeaderboardResponse;
import com.ewomen.greenfuture.dto.HotspotResponse;
import com.ewomen.greenfuture.dto.OperatorLeaderboardResponse;

import com.ewomen.greenfuture.service.AnalyticsService;
import com.ewomen.greenfuture.service.ChartAnalyticsService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasAuthority('can_view_dashboard')")
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final ChartAnalyticsService chartAnalyticsService;

    public AnalyticsController(
            AnalyticsService analyticsService,
            ChartAnalyticsService chartAnalyticsService) {

        this.analyticsService = analyticsService;
        this.chartAnalyticsService = chartAnalyticsService;
    }

    /*
     * =========================
     * DASHBOARD SUMMARY
     * =========================
     */

    @GetMapping("/dashboard")
    public List<AnalyticsResponse> dashboard() {

        return analyticsService.getDashboardStats();
    }

    /*
     * =========================
     * COMMUNITY LEADERBOARD
     * =========================
     */

    @GetMapping("/leaderboard/communities")
    public List<CommunityLeaderboardResponse> topCommunities() {

        return analyticsService.getTopCommunities();
    }

    /*
     * =========================
     * OPERATOR PERFORMANCE
     * =========================
     */

    @GetMapping("/leaderboard/operators")
    public List<OperatorLeaderboardResponse> topOperators() {

        return analyticsService.getTopOperators();
    }

    /*
     * =========================
     * HOTSPOTS
     * =========================
     */

    @GetMapping("/hotspots")
    public List<HotspotResponse> hotspots() {

        return analyticsService.getRecentHotspots();
    }

    /*
     * =========================
     * CHART: MONTHLY REPORTS
     * =========================
     */

    @GetMapping("/monthly-reports")
    public List<ChartDataProjection> monthlyReports() {

        return chartAnalyticsService.getMonthlyReports();
    }

    /*
     * =========================
     * CHART: COMMUNITY LEADERBOARD
     * =========================
     */

    @GetMapping("/community-leaderboard")
    public List<ChartDataProjection> communityLeaderboardChart() {

        return chartAnalyticsService.getCommunityLeaderboardChart();
    }

    /*
     * =========================
     * CHART: HOTSPOT TRENDS
     * =========================
     */

    @GetMapping("/hotspot-trends")
    public List<ChartDataProjection> hotspotTrends() {

        return chartAnalyticsService.getHotspotTrends();
    }
}
