package com.ewomen.greenfuture.dto;

public class ChartDataResponse {

    private String label;
    private Long value;

    public ChartDataResponse() {
    }

    public ChartDataResponse(String label, Long value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public Long getValue() {
        return value;
    }
}