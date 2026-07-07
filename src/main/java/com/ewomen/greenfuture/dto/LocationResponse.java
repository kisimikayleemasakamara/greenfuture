package com.ewomen.greenfuture.dto;

public class LocationResponse {

    private String title;

    private Double latitude;

    private Double longitude;

    public LocationResponse(
            String title,
            Double latitude,
            Double longitude) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
