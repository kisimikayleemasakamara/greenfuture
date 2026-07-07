package com.ewomen.greenfuture.dto;

public class WasteMapPoint {

    private String title;
    private Double latitude;
    private Double longitude;
    private String status;
    private String community;

    public WasteMapPoint(
            String title,
            Double latitude,
            Double longitude,
            String status,
            String community) {

        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.community = community;
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

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCommunity(String community) {
        this.community = community;
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

    public String getStatus() {
        return status;
    }

    public String getCommunity() {
        return community;
    }
}