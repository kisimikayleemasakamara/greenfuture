package com.ewomen.greenfuture.controller;

import com.ewomen.greenfuture.entity.WasteCollectionAssignment;

import com.ewomen.greenfuture.service.WasteCollectionAssignmentService;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
public class WasteCollectionAssignmentController {

    private final WasteCollectionAssignmentService assignmentService;

    public WasteCollectionAssignmentController(
            WasteCollectionAssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PreAuthorize("hasAuthority('can_manage_routes')")
    @PostMapping
    public WasteCollectionAssignment assign(
            @RequestBody WasteCollectionAssignment assignment) {
        return assignmentService.assign(assignment);
    }

    @PreAuthorize("hasAuthority('can_view_dashboard')")
    @GetMapping
    public List<WasteCollectionAssignment> getAll() {
        return assignmentService.getAll();
    }

    @PreAuthorize("hasAuthority('can_manage_routes')")
    @PutMapping("/{id}/complete")
    public WasteCollectionAssignment complete(
            @PathVariable Long id) {
        return assignmentService.completeAssignment(id);
    }
}