package com.ewomen.greenfuture.service;

import com.ewomen.greenfuture.dto.*;

import java.util.List;

public interface AnalyticsService {

    List<AnalyticsResponse> getDashboardStats();

    List<CommunityLeaderboardResponse> getTopCommunities();

    List<OperatorLeaderboardResponse> getTopOperators();

    List<HotspotResponse> getRecentHotspots();

    List<CommunityLeaderboardResponse> getCommunityLeaderboard();
}