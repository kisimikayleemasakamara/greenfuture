package com.ewomen.greenfuture.controller;

import com.ewomen.greenfuture.dto.CommunityLeaderboardResponse;
import com.ewomen.greenfuture.dto.PublicDashboardStatsResponse;
import com.ewomen.greenfuture.dto.PublicWasteReportRequest;
import com.ewomen.greenfuture.service.PublicService;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final PublicService publicService;

    public PublicController(PublicService publicService) {
        this.publicService = publicService;
    }

    // PUBLIC DATA
    @GetMapping("/stats")
    public PublicDashboardStatsResponse dashboardStats() {
        return publicService.getDashboardStats();
    }

    // PUBLIC DATA
    @GetMapping("/leaderboard")
    public List<CommunityLeaderboardResponse> leaderboard() {
        return publicService.getCommunityLeaderboard();
    }

    // PUBLIC INPUT (needs validation, not RBAC)
    @PostMapping(value = "/reports", consumes = "multipart/form-data")
    public String submitPublicReport(@ModelAttribute PublicWasteReportRequest request) {
        publicService.submitPublicReport(request);
        return "Public report submitted successfully.";
    }
}