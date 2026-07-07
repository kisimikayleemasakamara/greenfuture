package com.ewomen.greenfuture.dto;

public class OperatorLeaderboardResponse {

    private String operatorName;
    private Long completedAssignments;

    public OperatorLeaderboardResponse() {
    }

    public OperatorLeaderboardResponse(
            String operatorName,
            Long completedAssignments) {
        this.operatorName = operatorName;
        this.completedAssignments = completedAssignments;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public Long getCompletedAssignments() {
        return completedAssignments;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public void setCompletedAssignments(
            Long completedAssignments) {
        this.completedAssignments = completedAssignments;
    }
}