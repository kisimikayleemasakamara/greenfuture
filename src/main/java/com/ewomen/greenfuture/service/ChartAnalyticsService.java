package com.ewomen.greenfuture.service;

import com.ewomen.greenfuture.dto.ChartDataProjection;
import com.ewomen.greenfuture.dto.WasteMapPoint;
import com.ewomen.greenfuture.repository.WasteReportRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChartAnalyticsService {

    private final WasteReportRepository wasteReportRepository;

    public ChartAnalyticsService(

            WasteReportRepository wasteReportRepository

    ) {

        this.wasteReportRepository = wasteReportRepository;
    }

    public List<ChartDataProjection> getMonthlyReports() {

        return wasteReportRepository
                .getMonthlyReports();
    }

    public List<ChartDataProjection> getHotspotTrends() {

        return wasteReportRepository
                .getHotspotTrends();
    }

    public List<ChartDataProjection> getCommunityLeaderboardChart() {

        return wasteReportRepository
                .getCommunityLeaderboard();
    }

    public List<WasteMapPoint> getMapPoints() {
        return wasteReportRepository.getWasteMapPoints();
    }
}