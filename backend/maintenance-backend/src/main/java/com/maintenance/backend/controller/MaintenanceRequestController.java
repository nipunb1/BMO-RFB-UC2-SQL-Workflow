package com.maintenance.backend.controller;

import com.maintenance.backend.model.MaintenanceRequest;
import com.maintenance.backend.model.User;
import com.maintenance.backend.service.MaintenanceRequestService;
import com.maintenance.backend.service.UserService;
import com.maintenance.backend.service.SqlValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/requests")
@CrossOrigin(origins = "*")
public class MaintenanceRequestController {
    
    @Autowired
    private MaintenanceRequestService requestService;
    
    @Autowired
    private UserService userService;
    
    @GetMapping
    public ResponseEntity<List<MaintenanceRequest>> getAllRequests() {
        return ResponseEntity.ok(requestService.getAllRequests());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getRequestById(@PathVariable Long id) {
        Optional<MaintenanceRequest> request = requestService.getRequestById(id);
        if (request.isPresent()) {
            return ResponseEntity.ok(request.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getRequestsByUser(@PathVariable Long userId) {
        Optional<User> user = userService.getUserById(userId);
        if (user.isPresent()) {
            List<MaintenanceRequest> requests = requestService.getRequestsBySubmitter(user.get());
            return ResponseEntity.ok(requests);
        }
        return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MaintenanceRequest>> getRequestsByStatus(@PathVariable String status) {
        try {
            MaintenanceRequest.RequestStatus requestStatus = MaintenanceRequest.RequestStatus.valueOf(status.toUpperCase());
            return ResponseEntity.ok(requestService.getRequestsByStatus(requestStatus));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/pending-approval")
    public ResponseEntity<List<MaintenanceRequest>> getPendingApprovalRequests() {
        return ResponseEntity.ok(requestService.getPendingApprovalRequests());
    }
    
    @PostMapping
    public ResponseEntity<?> createRequest(@RequestBody MaintenanceRequest request, 
                                         @RequestHeader("Authorization") String token) {
        try {
            User submitter = getUserFromToken(token);
            MaintenanceRequest created = requestService.createRequest(request, submitter);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRequest(@PathVariable Long id, 
                                         @RequestBody MaintenanceRequest request,
                                         @RequestHeader("Authorization") String token) {
        try {
            User user = getUserFromToken(token);
            MaintenanceRequest updated = requestService.updateRequest(id, request, user);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitRequest(@PathVariable Long id, 
                                         @RequestHeader("Authorization") String token) {
        try {
            User submitter = getUserFromToken(token);
            MaintenanceRequest submitted = requestService.submitRequest(id, submitter);
            return ResponseEntity.ok(submitted);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/execute")
    public ResponseEntity<?> executeRequest(@PathVariable Long id, 
                                          @RequestHeader("Authorization") String token) {
        try {
            User executor = getUserFromToken(token);
            MaintenanceRequest executed = requestService.executeRequest(id, executor);
            return ResponseEntity.ok(executed);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PostMapping("/validate-sql")
    public ResponseEntity<?> validateSql(@RequestBody Map<String, String> request) {
        try {
            String sqlStatement = request.get("sqlStatement");
            if (sqlStatement == null || sqlStatement.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "SQL statement is required"));
            }
            
            SqlValidationService.SqlValidationResult result = requestService.validateSql(sqlStatement);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<?> getRequestStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("pendingRequests", requestService.getRequestCountByStatus(MaintenanceRequest.RequestStatus.PENDING_APPROVAL));
        stats.put("completedThisMonth", requestService.getRequestCountByStatus(MaintenanceRequest.RequestStatus.COMPLETED));
        stats.put("sqlFixesThisMonth", requestService.getRequestCountByTypeThisMonth(MaintenanceRequest.RequestType.SQL_FIX));
        stats.put("configUpdatesThisMonth", requestService.getRequestCountByTypeThisMonth(MaintenanceRequest.RequestType.CONFIG_UPDATE));
        stats.put("jobControlsThisMonth", requestService.getRequestCountByTypeThisMonth(MaintenanceRequest.RequestType.JOB_CONTROL));
        stats.put("logManagementThisMonth", requestService.getRequestCountByTypeThisMonth(MaintenanceRequest.RequestType.LOG_MANAGEMENT));
        stats.put("patchDeploymentThisMonth", requestService.getRequestCountByTypeThisMonth(MaintenanceRequest.RequestType.PATCH_DEPLOYMENT));
        
        return ResponseEntity.ok(stats);
    }
    
    private User getUserFromToken(String token) {
        String userId = token.replace("Bearer mock-jwt-token-", "");
        Optional<User> userOpt = userService.getUserById(Long.parseLong(userId));
        if (userOpt.isPresent()) {
            return userOpt.get();
        }
        throw new RuntimeException("Invalid token or user not found");
    }
}
