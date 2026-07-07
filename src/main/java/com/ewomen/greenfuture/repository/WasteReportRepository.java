package com.ewomen.greenfuture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ewomen.greenfuture.dto.ChartDataProjection;
import com.ewomen.greenfuture.dto.WasteMapPoint;
import com.ewomen.greenfuture.entity.ReportStatus;
import com.ewomen.greenfuture.entity.WasteReport;

public interface WasteReportRepository extends JpaRepository<WasteReport, Long> {

    long count();

    long countByStatus(ReportStatus status);

    List<WasteReport> findTop5ByOrderByCreatedAtDesc();

    @Query(value = """
            SELECT
                TO_CHAR(created_at, 'Mon') AS name,
                COUNT(*) AS value
            FROM waste_reports
            GROUP BY TO_CHAR(created_at, 'Mon'),
                     EXTRACT(MONTH FROM created_at)
            ORDER BY EXTRACT(MONTH FROM created_at)
            """, nativeQuery = true)
    List<ChartDataProjection> getMonthlyReports();

    @Query("""
                SELECT
                    w.community.name AS name,
                    COUNT(w) AS value
                FROM WasteReport w
                GROUP BY w.community.name
                ORDER BY COUNT(w) DESC
            """)
    List<ChartDataProjection> getCommunityLeaderboard();

    @Query("""
                SELECT
                    w.location AS name,
                    COUNT(w) AS value
                FROM WasteReport w
                GROUP BY w.location
                ORDER BY COUNT(w) DESC
            """)
    List<ChartDataProjection> getHotspotTrends();

    @Query("""
            SELECT new com.ewomen.greenfuture.dto.WasteMapPoint(
                w.title,
                w.latitude,
                w.longitude,
                CAST(w.status as string),
                w.community.name
            )
            FROM WasteReport w
            WHERE w.latitude IS NOT NULL
            AND w.longitude IS NOT NULL
            """)
    List<WasteMapPoint> getWasteMapPoints();
}
