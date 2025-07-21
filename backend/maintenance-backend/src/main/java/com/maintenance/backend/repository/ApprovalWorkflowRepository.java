package com.maintenance.backend.repository;

import com.maintenance.backend.model.ApprovalWorkflow;
import com.maintenance.backend.model.MaintenanceRequest;
import com.maintenance.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalWorkflowRepository extends JpaRepository<ApprovalWorkflow, Long> {
    List<ApprovalWorkflow> findByRequest(MaintenanceRequest request);
    List<ApprovalWorkflow> findByApprover(User approver);
    List<ApprovalWorkflow> findByStatus(ApprovalWorkflow.ApprovalStatus status);
    Optional<ApprovalWorkflow> findByRequestAndType(MaintenanceRequest request, ApprovalWorkflow.ApprovalType type);
    List<ApprovalWorkflow> findByApproverAndStatus(User approver, ApprovalWorkflow.ApprovalStatus status);
}
