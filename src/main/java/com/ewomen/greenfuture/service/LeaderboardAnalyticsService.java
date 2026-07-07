package com.ewomen.greenfuture.service;

import com.ewomen.greenfuture.dto.*;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardAnalyticsService {

    private final AnalyticsService analyticsService;

    public LeaderboardAnalyticsService(
            AnalyticsService analyticsService) {

        this.analyticsService = analyticsService;
    }

    public List<CommunityLeaderboardResponse> getTopCommunities() {

        return analyticsService
                .getTopCommunities();
    }

    public List<OperatorLeaderboardResponse> getTopOperators() {

        return analyticsService
                .getTopOperators();
    }
}