package com.ewomen.greenfuture.dto;

import org.springframework.web.multipart.MultipartFile;

public class PublicWasteReportRequest {

    private MultipartFile image;
    private String description;
    private Double latitude;
    private Double longitude;
    private String reporterName;
    private String phoneNumber;

    public PublicWasteReportRequest() {
    }

    // IMAGE
    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }

    // DESCRIPTION
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // LATITUDE
    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    // LONGITUDE
    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    // REPORTER NAME
    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    // PHONE NUMBER
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}