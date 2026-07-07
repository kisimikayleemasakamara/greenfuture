package com.ewomen.greenfuture.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "waste_reports")
public class WasteReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private ReportStatus status;
    private String location;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private LocalDateTime createdAt;
    private boolean publicSubmission;
    private String publicReporterName;
    private String publicPhoneNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "community_id")
    private Community community;

    public WasteReport() {
    }

    public WasteReport(String title, String description, ReportStatus status,
            String location, Community community, double longitude, double latitude, LocalDateTime createdAt,
            boolean publicSubmission, String publicReporterName, String publicPhoneNumber) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.location = location;
        this.community = community;
        this.latitude = latitude;
        this.location = location;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.publicSubmission = publicSubmission;
        this.publicReporterName = publicReporterName;
        this.publicPhoneNumber = publicPhoneNumber;
    }

    public Long getId() {
        return this.id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isPublicSubmission() {
        return publicSubmission;
    }

    public void setPublicSubmission(boolean publicSubmission) {
        this.publicSubmission = publicSubmission;
    }

    public String getPublicReporterName() {
        return publicReporterName;
    }

    public void setPublicReporterName(String publicReporterName) {
        this.publicReporterName = publicReporterName;
    }

    public String getPublicPhoneNumber() {
        return publicPhoneNumber;
    }

    public void setPublicPhoneNumber(String publicPhoneNumber) {
        this.publicPhoneNumber = publicPhoneNumber;
    }

}
