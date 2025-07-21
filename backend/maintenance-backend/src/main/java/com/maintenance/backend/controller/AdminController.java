package com.maintenance.backend.controller;

import com.maintenance.backend.model.User;
import com.maintenance.backend.service.UserService;
import com.maintenance.backend.service.MaintenanceRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MaintenanceRequestService requestService;
    
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/users/role/{role}")
    public ResponseEntity<?> getUsersByRole(@PathVariable String role) {
        try {
            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            List<User> users = userService.getUsersByRole(userRole);
            return ResponseEntity.ok(users);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role: " + role));
        }
    }
    
    @GetMapping("/users/managers")
    public ResponseEntity<List<User>> getManagers() {
        return ResponseEntity.ok(userService.getManagers());
    }
    
    @GetMapping("/users/developers")
    public ResponseEntity<List<User>> getDevelopers() {
        return ResponseEntity.ok(userService.getDevelopers());
    }
    
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User created = userService.createUser(user);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updated = userService.updateUser(id, user);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        analytics.put("totalUsers", userService.getAllUsers().size());
        analytics.put("activeUsers", userService.getActiveUsers().size());
        analytics.put("totalRequests", requestService.getAllRequests().size());
        analytics.put("pendingRequests", requestService.getPendingApprovalRequests().size());
        
        Map<String, Object> requestsByType = new HashMap<>();
        requestsByType.put("sqlFixes", requestService.getRequestCountByTypeThisMonth(com.maintenance.backend.model.MaintenanceRequest.RequestType.SQL_FIX));
        requestsByType.put("configUpdates", requestService.getRequestCountByTypeThisMonth(com.maintenance.backend.model.MaintenanceRequest.RequestType.CONFIG_UPDATE));
        requestsByType.put("jobControls", requestService.getRequestCountByTypeThisMonth(com.maintenance.backend.model.MaintenanceRequest.RequestType.JOB_CONTROL));
        requestsByType.put("logManagement", requestService.getRequestCountByTypeThisMonth(com.maintenance.backend.model.MaintenanceRequest.RequestType.LOG_MANAGEMENT));
        requestsByType.put("patchDeployment", requestService.getRequestCountByTypeThisMonth(com.maintenance.backend.model.MaintenanceRequest.RequestType.PATCH_DEPLOYMENT));
        
        analytics.put("requestsByType", requestsByType);
        
        return ResponseEntity.ok(analytics);
    }
}
