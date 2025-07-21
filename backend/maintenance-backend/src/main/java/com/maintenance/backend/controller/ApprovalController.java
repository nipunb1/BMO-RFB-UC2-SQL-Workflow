package com.maintenance.backend.controller;

import com.maintenance.backend.model.ApprovalWorkflow;
import com.maintenance.backend.model.MaintenanceRequest;
import com.maintenance.backend.model.User;
import com.maintenance.backend.service.ApprovalWorkflowService;
import com.maintenance.backend.service.UserService;
import com.maintenance.backend.service.MaintenanceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/approvals")
@CrossOrigin(origins = "*")
public class ApprovalController {
    
    @Autowired
    private ApprovalWorkflowService approvalService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MaintenanceRequestService requestService;
    
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingApprovals(@RequestHeader("Authorization") String token) {
        try {
            User approver = getUserFromToken(token);
            List<ApprovalWorkflow> approvals = approvalService.getPendingApprovals(approver);
            return ResponseEntity.ok(approvals);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/request/{requestId}")
    public ResponseEntity<?> getApprovalsByRequest(@PathVariable Long requestId) {
        Optional<MaintenanceRequest> request = requestService.getRequestById(requestId);
        if (request.isPresent()) {
            List<ApprovalWorkflow> approvals = approvalService.getApprovalsByRequest(request.get());
            return ResponseEntity.ok(approvals);
        }
        return ResponseEntity.notFound().build();
    }
    
    @PostMapping("/peer-review")
    public ResponseEntity<?> createPeerReview(@RequestBody PeerReviewRequest request,
                                            @RequestHeader("Authorization") String token) {
        try {
            User currentUser = getUserFromToken(token);
            
            Optional<MaintenanceRequest> maintenanceRequest = requestService.getRequestById(request.getRequestId());
            Optional<User> peerReviewer = userService.getUserById(request.getPeerReviewerId());
            
            if (maintenanceRequest.isPresent() && peerReviewer.isPresent()) {
                ApprovalWorkflow approval = approvalService.createPeerReview(
                    maintenanceRequest.get(), peerReviewer.get());
                return ResponseEntity.ok(approval);
            }
            
            return ResponseEntity.badRequest().body(Map.of("error", "Request or peer reviewer not found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/manager-approval")
    public ResponseEntity<?> createManagerApproval(@RequestBody ManagerApprovalRequest request,
                                                 @RequestHeader("Authorization") String token) {
        try {
            User currentUser = getUserFromToken(token);
            
            Optional<MaintenanceRequest> maintenanceRequest = requestService.getRequestById(request.getRequestId());
            Optional<User> manager = userService.getUserById(request.getManagerId());
            
            if (maintenanceRequest.isPresent() && manager.isPresent()) {
                ApprovalWorkflow approval = approvalService.createManagerApproval(
                    maintenanceRequest.get(), manager.get());
                return ResponseEntity.ok(approval);
            }
            
            return ResponseEntity.badRequest().body(Map.of("error", "Request or manager not found"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{approvalId}/process")
    public ResponseEntity<?> processApproval(@PathVariable Long approvalId,
                                           @RequestBody ApprovalDecision decision,
                                           @RequestHeader("Authorization") String token) {
        try {
            User approver = getUserFromToken(token);
            
            ApprovalWorkflow.ApprovalStatus status = ApprovalWorkflow.ApprovalStatus.valueOf(
                decision.getDecision().toUpperCase());
            
            ApprovalWorkflow processed = approvalService.processApproval(
                approvalId, status, decision.getComments(), approver);
            
            return ResponseEntity.ok(processed);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    private User getUserFromToken(String token) {
        String userId = token.replace("Bearer mock-jwt-token-", "");
        Optional<User> userOpt = userService.getUserById(Long.parseLong(userId));
        if (userOpt.isPresent()) {
            return userOpt.get();
        }
        throw new RuntimeException("Invalid token or user not found");
    }
    
    public static class PeerReviewRequest {
        private Long requestId;
        private Long peerReviewerId;

        public Long getRequestId() {
            return requestId;
        }

        public void setRequestId(Long requestId) {
            this.requestId = requestId;
        }

        public Long getPeerReviewerId() {
            return peerReviewerId;
        }

        public void setPeerReviewerId(Long peerReviewerId) {
            this.peerReviewerId = peerReviewerId;
        }
    }
    
    public static class ManagerApprovalRequest {
        private Long requestId;
        private Long managerId;

        public Long getRequestId() {
            return requestId;
        }

        public void setRequestId(Long requestId) {
            this.requestId = requestId;
        }

        public Long getManagerId() {
            return managerId;
        }

        public void setManagerId(Long managerId) {
            this.managerId = managerId;
        }
    }
    
    public static class ApprovalDecision {
        private String decision;
        private String comments;

        public String getDecision() {
            return decision;
        }

        public void setDecision(String decision) {
            this.decision = decision;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }
    }
}
