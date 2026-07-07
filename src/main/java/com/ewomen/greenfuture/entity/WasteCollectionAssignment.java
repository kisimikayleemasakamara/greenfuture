package com.ewomen.greenfuture.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class WasteCollectionAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private WasteReport wasteReport;

    @ManyToOne
    @JoinColumn(name = "ecotrike_id")
    private EcoTrike ecoTrike;

    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;

    private LocalDateTime assignedAt;

    public WasteCollectionAssignment() {
        this.assignedAt = LocalDateTime.now();
        this.status = AssignmentStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public WasteReport getWasteReport() {
        return wasteReport;
    }

    public void setWasteReport(WasteReport wasteReport) {
        this.wasteReport = wasteReport;
    }

    public EcoTrike getEcoTrike() {
        return ecoTrike;
    }

    public void setEcoTrike(EcoTrike ecoTrike) {
        this.ecoTrike = ecoTrike;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }
}
