package com.ewomen.greenfuture.service;

import com.ewomen.greenfuture.entity.*;
import com.ewomen.greenfuture.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WasteCollectionAssignmentService {

    private final WasteCollectionAssignmentRepository assignmentRepository;
    private final NotificationService notificationService;
    private final CommunityScoreService communityScoreService;

    public WasteCollectionAssignmentService(
            WasteCollectionAssignmentRepository assignmentRepository,
            NotificationService notificationService, CommunityScoreService communityScoreService) {
        this.assignmentRepository = assignmentRepository;
        this.notificationService = notificationService;
        this.communityScoreService = communityScoreService;
    }

    public WasteCollectionAssignment assign(
            WasteCollectionAssignment assignment) {

        assignment.setStatus(AssignmentStatus.ASSIGNED);

        WasteCollectionAssignment savedAssignment = assignmentRepository.save(assignment);

        // Notify operator
        if (assignment.getEcoTrike() != null &&
                assignment.getEcoTrike().getOperator() != null) {

            notificationService.createNotification(
                    "New Waste Collection Assignment",
                    "You have been assigned a waste collection task.",
                    assignment.getEcoTrike().getOperator());
        }

        return savedAssignment;
    }

    public List<WasteCollectionAssignment> getAll() {
        return assignmentRepository.findAll();
    }

    public WasteCollectionAssignment completeAssignment(
            Long assignmentId) {

        WasteCollectionAssignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow();

        // Update assignment status
        assignment.setStatus(AssignmentStatus.COMPLETED);

        // Update waste report
        WasteReport report = assignment.getWasteReport();

        report.setStatus(ReportStatus.RESOLVED);

        // Reward community
        if (report.getCommunity() != null) {

            communityScoreService.rewardResolvedWaste(
                    report.getCommunity());
        }

        WasteCollectionAssignment updatedAssignment = assignmentRepository.save(assignment);

        // Notify operator/community
        if (assignment.getEcoTrike() != null &&
                assignment.getEcoTrike().getOperator() != null) {

            notificationService.createNotification(
                    "Collection Completed",
                    "Waste collection task completed successfully.",
                    assignment.getEcoTrike().getOperator());
        }

        return updatedAssignment;
    }
}
