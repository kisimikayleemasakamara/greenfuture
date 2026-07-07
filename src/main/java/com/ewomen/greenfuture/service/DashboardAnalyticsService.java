package com.ewomen.greenfuture.service;

import com.ewomen.greenfuture.dto.AnalyticsResponse;

import com.ewomen.greenfuture.repository.*;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DashboardAnalyticsService {

    private final UserRepository userRepository;

    private final WasteReportRepository wasteReportRepository;

    private final CommunityRepository communityRepository;

    private final EcoTrikeRepository ecoTrikeRepository;

    public DashboardAnalyticsService(

            UserRepository userRepository,

            WasteReportRepository wasteReportRepository,

            CommunityRepository communityRepository,

            EcoTrikeRepository ecoTrikeRepository

    ) {

        this.userRepository = userRepository;

        this.wasteReportRepository = wasteReportRepository;

        this.communityRepository = communityRepository;

        this.ecoTrikeRepository = ecoTrikeRepository;
    }

    public List<AnalyticsResponse> getDashboardStats() {

        List<AnalyticsResponse> stats = new ArrayList<>();

        stats.add(new AnalyticsResponse(
                "Total Users",
                userRepository.count()));

        stats.add(new AnalyticsResponse(
                "Total Reports",
                wasteReportRepository.count()));

        stats.add(new AnalyticsResponse(
                "Communities",
                communityRepository.count()));

        stats.add(new AnalyticsResponse(
                "EcoTrikes",
                ecoTrikeRepository.count()));

        return stats;
    }
}