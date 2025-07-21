package com.maintenance.backend.service;

import com.maintenance.backend.model.ApprovalWorkflow;
import com.maintenance.backend.model.MaintenanceRequest;
import com.maintenance.backend.model.User;
import com.maintenance.backend.model.AuditLog;
import com.maintenance.backend.repository.ApprovalWorkflowRepository;
import com.maintenance.backend.repository.MaintenanceRequestRepository;
import com.maintenance.backend.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ApprovalWorkflowService {
    
    @Autowired
    private ApprovalWorkflowRepository approvalRepository;
    
    @Autowired
    private MaintenanceRequestRepository requestRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    public List<ApprovalWorkflow> getPendingApprovals(User approver) {
        return approvalRepository.findByApproverAndStatus(approver, ApprovalWorkflow.ApprovalStatus.PENDING);
    }
    
    public List<ApprovalWorkflow> getApprovalsByRequest(MaintenanceRequest request) {
        return approvalRepository.findByRequest(request);
    }
    
    public ApprovalWorkflow createPeerReview(MaintenanceRequest request, User peerReviewer) {
        ApprovalWorkflow approval = new ApprovalWorkflow();
        approval.setRequest(request);
        approval.setApprover(peerReviewer);
        approval.setType(ApprovalWorkflow.ApprovalType.PEER_REVIEW);
        approval.setStatus(ApprovalWorkflow.ApprovalStatus.PENDING);
        
        ApprovalWorkflow saved = approvalRepository.save(approval);
        
        request.setStatus(MaintenanceRequest.RequestStatus.PEER_REVIEW);
        requestRepository.save(request);
        
        logAudit(request, peerReviewer, AuditLog.ActionType.PEER_REVIEW_ASSIGNED, 
                "Peer review assigned to: " + peerReviewer.getName());
        
        return saved;
    }
    
    public ApprovalWorkflow createManagerApproval(MaintenanceRequest request, User manager) {
        ApprovalWorkflow approval = new ApprovalWorkflow();
        approval.setRequest(request);
        approval.setApprover(manager);
        approval.setType(ApprovalWorkflow.ApprovalType.MANAGER_APPROVAL);
        approval.setStatus(ApprovalWorkflow.ApprovalStatus.PENDING);
        
        ApprovalWorkflow saved = approvalRepository.save(approval);
        
        request.setStatus(MaintenanceRequest.RequestStatus.PENDING_APPROVAL);
        requestRepository.save(request);
        
        logAudit(request, manager, AuditLog.ActionType.APPROVAL_REQUESTED, 
                "Manager approval requested from: " + manager.getName());
        
        return saved;
    }
    
    public ApprovalWorkflow processApproval(Long approvalId, ApprovalWorkflow.ApprovalStatus decision, 
                                          String comments, User approver) {
        Optional<ApprovalWorkflow> approvalOpt = approvalRepository.findById(approvalId);
        if (approvalOpt.isPresent()) {
            ApprovalWorkflow approval = approvalOpt.get();
            
            approval.setStatus(decision);
            approval.setComments(comments);
            approval.setApprovedAt(LocalDateTime.now());
            
            ApprovalWorkflow saved = approvalRepository.save(approval);
            
            updateRequestStatus(approval.getRequest(), decision, approval.getType());
            
            AuditLog.ActionType auditAction = decision == ApprovalWorkflow.ApprovalStatus.APPROVED ? 
                    AuditLog.ActionType.APPROVAL_GRANTED : AuditLog.ActionType.APPROVAL_REJECTED;
            
            String auditDetails = approval.getType().toString() + " " + decision.toString().toLowerCase() + 
                    " by " + approver.getName() + (comments != null ? ": " + comments : "");
            
            logAudit(approval.getRequest(), approver, auditAction, auditDetails);
            
            return saved;
        }
        throw new RuntimeException("Approval not found with id: " + approvalId);
    }
    
    private void updateRequestStatus(MaintenanceRequest request, ApprovalWorkflow.ApprovalStatus decision, 
                                   ApprovalWorkflow.ApprovalType type) {
        if (decision == ApprovalWorkflow.ApprovalStatus.REJECTED) {
            request.setStatus(MaintenanceRequest.RequestStatus.REJECTED);
        } else if (decision == ApprovalWorkflow.ApprovalStatus.MORE_INFO_REQUIRED) {
            request.setStatus(MaintenanceRequest.RequestStatus.DRAFT);
        } else if (decision == ApprovalWorkflow.ApprovalStatus.APPROVED) {
            if (type == ApprovalWorkflow.ApprovalType.PEER_REVIEW) {
                request.setStatus(MaintenanceRequest.RequestStatus.PENDING_APPROVAL);
            } else if (type == ApprovalWorkflow.ApprovalType.MANAGER_APPROVAL) {
                request.setStatus(MaintenanceRequest.RequestStatus.APPROVED);
            }
        }
        
        requestRepository.save(request);
    }
    
    private void logAudit(MaintenanceRequest request, User user, AuditLog.ActionType action, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setRequest(request);
        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setDetails(details);
        auditLog.setIpAddress("127.0.0.1");
        auditLog.setUserAgent("MaintenanceApp/1.0");
        auditLogRepository.save(auditLog);
    }
}
