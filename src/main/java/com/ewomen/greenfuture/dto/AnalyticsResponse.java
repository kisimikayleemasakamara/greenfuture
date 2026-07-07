package com.ewomen.greenfuture.dto;

public class AnalyticsResponse {

    private String label;

    private Long value;

    public AnalyticsResponse() {
    }

    public AnalyticsResponse(
            String label,
            Long value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public Long getValue() {
        return value;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(Long value) {
        this.value = value;
    }
}