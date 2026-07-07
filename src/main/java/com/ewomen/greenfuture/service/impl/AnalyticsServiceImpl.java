package com.ewomen.greenfuture.service.impl;

import com.ewomen.greenfuture.dto.*;
import com.ewomen.greenfuture.entity.ReportStatus;
import com.ewomen.greenfuture.repository.*;
import com.ewomen.greenfuture.service.AnalyticsService;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnalyticsServiceImpl
        implements AnalyticsService {

    private final WasteReportRepository wasteReportRepository;
    private final CommunityRepository communityRepository;
    private final WasteCollectionAssignmentRepository assignmentRepository;
    private final EcoTrikeRepository ecoTrikeRepository;
    private final UserRepository userRepository;

    public AnalyticsServiceImpl(
            WasteReportRepository wasteReportRepository,
            CommunityRepository communityRepository,
            WasteCollectionAssignmentRepository assignmentRepository,
            EcoTrikeRepository ecoTrikeRepository,
            UserRepository userRepository) {

        this.wasteReportRepository = wasteReportRepository;
        this.communityRepository = communityRepository;
        this.assignmentRepository = assignmentRepository;
        this.ecoTrikeRepository = ecoTrikeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<AnalyticsResponse> getDashboardStats() {

        List<AnalyticsResponse> stats = new ArrayList<>();

        stats.add(
                new AnalyticsResponse(
                        "Total Reports",
                        wasteReportRepository.count()));

        stats.add(
                new AnalyticsResponse(
                        "Resolved Reports",
                        wasteReportRepository.countByStatus(
                                ReportStatus.RESOLVED)));

        stats.add(
                new AnalyticsResponse(
                        "Pending Reports",
                        wasteReportRepository.countByStatus(
                                ReportStatus.PENDING)));

        stats.add(
                new AnalyticsResponse(
                        "Communities",
                        communityRepository.count()));

        stats.add(
                new AnalyticsResponse(
                        "EcoTrikes",
                        ecoTrikeRepository.count()));

        stats.add(
                new AnalyticsResponse(
                        "Assignments",
                        assignmentRepository.count()));

        return stats;
    }

    @Override
    public List<CommunityLeaderboardResponse> getTopCommunities() {

        return communityRepository
                .findTopCommunities()
                .stream()
                .map(community -> new CommunityLeaderboardResponse(
                        community.getName(),
                        community.getScore()))
                .toList();
    }

    @Override
    public List<OperatorLeaderboardResponse> getTopOperators() {

        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole().name()
                        .equals("ECOTRIKE_OPERATOR"))

                .map(user -> new OperatorLeaderboardResponse(
                        user.getFullName(),
                        assignmentRepository
                                .countByEcoTrike_Operator_Id(
                                        user.getId())))

                .sorted((a, b) -> Long.compare(
                        b.getCompletedAssignments(),
                        a.getCompletedAssignments()))

                .toList();
    }

    @Override
    public List<HotspotResponse> getRecentHotspots() {

        return wasteReportRepository
                .findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(report -> new HotspotResponse(
                        report.getTitle(),
                        report.getLatitude(),
                        report.getLongitude()))
                .toList();
    }

    @Override
    public List<CommunityLeaderboardResponse> getCommunityLeaderboard() {

        return communityRepository.findAll()
                .stream()

                .sorted((a, b) -> Integer.compare(
                        b.getScore(),
                        a.getScore()))

                .map(community -> new CommunityLeaderboardResponse(
                        community.getName(),
                        community.getScore()))

                .toList();
    }
}