package com.ewomen.greenfuture.repository;

import com.ewomen.greenfuture.entity.WasteCollectionAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WasteCollectionAssignmentRepository
                extends JpaRepository<WasteCollectionAssignment, Long> {
        long countByEcoTrike_Operator_Id(Long operatorId);
}