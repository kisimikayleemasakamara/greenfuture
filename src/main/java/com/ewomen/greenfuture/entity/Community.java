package com.ewomen.greenfuture.entity;

import jakarta.persistence.*;

@Entity
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private Integer score;
    private String district;
    private int ranking;
    private int wasteReports;
    private int cleanUpActivities;
    private int resolvedReports;

    // Constructor without parameters
    public Community() {
    }

    // Constructor with parameters
    public Community(String name, int score, String district, int ranking) {
        this.name = name;
        this.score = score;
        this.district = district;
        this.ranking = ranking;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public int getWasteReports() {
        return wasteReports;
    }

    public void setWasteReports(int wasteReports) {
        this.wasteReports = wasteReports;
    }

    public int getCleanUpActivities() {
        return cleanUpActivities;
    }

    public void setCleanUpActivities(int cleanUpActivities) {
        this.cleanUpActivities = cleanUpActivities;
    }

    public int getResolvedReports() {
        return resolvedReports;
    }

    public void setResolvedReports(int resolvedReports) {
        this.resolvedReports = resolvedReports;
    }

}
