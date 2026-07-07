package com.ewomen.greenfuture.dto;

public class HotspotResponse {

    private String title;
    private Double latitude;
    private Double longitude;

    public HotspotResponse() {
    }

    public HotspotResponse(
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

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
