package com.ewomen.greenfuture.dto;

public class PublicDashboardStatsResponse {

    private long totalReports;
    private long totalUsers;
    private long totalResolved;
    private long totalPending;
    private long totalPoints;

    // Empty constructor
    public PublicDashboardStatsResponse() {
    }

    // Constructor with parameters
    public PublicDashboardStatsResponse(
            long totalReports,
            long totalUsers,
            long totalResolved,
            long totalPending,
            long totalPoints) {

        this.totalReports = totalReports;
        this.totalUsers = totalUsers;
        this.totalResolved = totalResolved;
        this.totalPending = totalPending;
        this.totalPoints = totalPoints;
    }

    // Getters and Setters

    public long getTotalReports() {
        return totalReports;
    }

    public void setTotalReports(long totalReports) {
        this.totalReports = totalReports;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalResolved() {
        return totalResolved;
    }

    public void setTotalResolved(long totalResolved) {
        this.totalResolved = totalResolved;
    }

    public long getTotalPending() {
        return totalPending;
    }

    public void setTotalPending(long totalPending) {
        this.totalPending = totalPending;
    }

    public long getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(long totalPoints) {
        this.totalPoints = totalPoints;
    }
}