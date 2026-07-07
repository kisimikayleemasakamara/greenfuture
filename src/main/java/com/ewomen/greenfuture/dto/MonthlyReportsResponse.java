package com.ewomen.greenfuture.dto;

public class MonthlyReportsResponse {

    private String month;
    private long reports;

    public MonthlyReportsResponse(
            String month,
            long reports) {

        this.month = month;
        this.reports = reports;
    }

    public String getMonth() {
        return month;
    }

    public long getReports() {
        return reports;
    }
}