package com.ewomen.greenfuture.dto;

public class CommunityLeaderboardResponse {

    private String communityName;
    private Integer score;

    public CommunityLeaderboardResponse(
            String communityName,
            Integer score) {
        this.communityName = communityName;
        this.score = score;
    }

    public String getCommunityName() {
        return communityName;
    }

    public Integer getScore() {
        return score;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}