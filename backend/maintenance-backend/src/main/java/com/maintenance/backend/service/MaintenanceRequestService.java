package com.maintenance.backend.service;

import com.maintenance.backend.model.MaintenanceRequest;
import com.maintenance.backend.model.User;
import com.maintenance.backend.model.AuditLog;
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
public class MaintenanceRequestService {
    
    @Autowired
    private MaintenanceRequestRepository requestRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    @Autowired
    private SqlValidationService sqlValidationService;
    
    public List<MaintenanceRequest> getAllRequests() {
        return requestRepository.findAll();
    }
    
    public Optional<MaintenanceRequest> getRequestById(Long id) {
        return requestRepository.findById(id);
    }
    
    public List<MaintenanceRequest> getRequestsBySubmitter(User submitter) {
        return requestRepository.findBySubmitter(submitter);
    }
    
    public List<MaintenanceRequest> getRequestsByStatus(MaintenanceRequest.RequestStatus status) {
        return requestRepository.findByStatus(status);
    }
    
    public List<MaintenanceRequest> getPendingApprovalRequests() {
        return requestRepository.findPendingApprovalRequests();
    }
    
    public MaintenanceRequest createRequest(MaintenanceRequest request, User submitter) {
        request.setSubmitter(submitter);
        request.setStatus(MaintenanceRequest.RequestStatus.DRAFT);
        
        MaintenanceRequest savedRequest = requestRepository.save(request);
        
        logAudit(savedRequest, submitter, AuditLog.ActionType.REQUEST_CREATED, 
                "Request created: " + request.getTitle());
        
        return savedRequest;
    }
    
    public MaintenanceRequest updateRequest(Long id, MaintenanceRequest updatedRequest, User user) {
        Optional<MaintenanceRequest> existingOpt = requestRepository.findById(id);
        if (existingOpt.isPresent()) {
            MaintenanceRequest existing = existingOpt.get();
            
            existing.setTitle(updatedRequest.getTitle());
            existing.setDescription(updatedRequest.getDescription());
            existing.setBusinessJustification(updatedRequest.getBusinessJustification());
            existing.setRollbackPlan(updatedRequest.getRollbackPlan());
            existing.setSqlStatement(updatedRequest.getSqlStatement());
            existing.setConfigContent(updatedRequest.getConfigContent());
            existing.setJobDetails(updatedRequest.getJobDetails());
            existing.setPriority(updatedRequest.getPriority());
            existing.setEnvironment(updatedRequest.getEnvironment());
            existing.setApplication(updatedRequest.getApplication());
            existing.setPeerReviewer(updatedRequest.getPeerReviewer());
            
            MaintenanceRequest saved = requestRepository.save(existing);
            
            logAudit(saved, user, AuditLog.ActionType.REQUEST_UPDATED, 
                    "Request updated: " + existing.getTitle());
            
            return saved;
        }
        throw new RuntimeException("Request not found with id: " + id);
    }
    
    public MaintenanceRequest submitRequest(Long id, User submitter) {
        Optional<MaintenanceRequest> requestOpt = requestRepository.findById(id);
        if (requestOpt.isPresent()) {
            MaintenanceRequest request = requestOpt.get();
            
            if (request.getType() == MaintenanceRequest.RequestType.SQL_FIX && 
                request.getSqlStatement() != null && !request.getSqlStatement().trim().isEmpty()) {
                
                SqlValidationService.SqlValidationResult validationResult = 
                    sqlValidationService.validateSql(request.getSqlStatement());
                
                request.setValidationResult(formatValidationResult(validationResult));
                request.setAffectedRows(validationResult.getAffectedRows());
                request.setExecutionTime(validationResult.getEstimatedExecutionTime());
                
                if (!validationResult.isValid()) {
                    throw new RuntimeException("SQL validation failed: " + validationResult.getMessage());
                }
            }
            
            request.setStatus(MaintenanceRequest.RequestStatus.SUBMITTED);
            MaintenanceRequest saved = requestRepository.save(request);
            
            logAudit(saved, submitter, AuditLog.ActionType.REQUEST_SUBMITTED, 
                    "Request submitted for approval: " + request.getTitle());
            
            return saved;
        }
        throw new RuntimeException("Request not found with id: " + id);
    }
    
    public SqlValidationService.SqlValidationResult validateSql(String sqlStatement) {
        return sqlValidationService.validateSql(sqlStatement);
    }
    
    public MaintenanceRequest executeRequest(Long id, User executor) {
        Optional<MaintenanceRequest> requestOpt = requestRepository.findById(id);
        if (requestOpt.isPresent()) {
            MaintenanceRequest request = requestOpt.get();
            
            if (request.getStatus() != MaintenanceRequest.RequestStatus.APPROVED) {
                throw new RuntimeException("Request must be approved before execution");
            }
            
            request.setStatus(MaintenanceRequest.RequestStatus.IN_PROGRESS);
            requestRepository.save(request);
            
            logAudit(request, executor, AuditLog.ActionType.EXECUTION_STARTED, 
                    "Request execution started");
            
            try {
                String executionResult = simulateExecution(request);
                request.setExecutionResult(executionResult);
                request.setStatus(MaintenanceRequest.RequestStatus.COMPLETED);
                request.setCompletedAt(LocalDateTime.now());
                
                MaintenanceRequest saved = requestRepository.save(request);
                
                logAudit(saved, executor, AuditLog.ActionType.EXECUTION_COMPLETED, 
                        "Request execution completed successfully");
                
                return saved;
                
            } catch (Exception e) {
                request.setStatus(MaintenanceRequest.RequestStatus.FAILED);
                request.setExecutionResult("Execution failed: " + e.getMessage());
                
                MaintenanceRequest saved = requestRepository.save(request);
                
                logAudit(saved, executor, AuditLog.ActionType.EXECUTION_FAILED, 
                        "Request execution failed: " + e.getMessage());
                
                throw new RuntimeException("Execution failed: " + e.getMessage());
            }
        }
        throw new RuntimeException("Request not found with id: " + id);
    }
    
    private String simulateExecution(MaintenanceRequest request) {
        StringBuilder result = new StringBuilder();
        result.append("[").append(LocalDateTime.now()).append("] Starting execution...\n");
        
        if (request.getType() == MaintenanceRequest.RequestType.SQL_FIX) {
            result.append("[").append(LocalDateTime.now()).append("] Backup created: ")
                  .append(request.getApplication().toLowerCase().replaceAll(" ", "_"))
                  .append("_backup_").append(System.currentTimeMillis()).append("\n");
            result.append("[").append(LocalDateTime.now()).append("] Executing SQL statement...\n");
            result.append("[").append(LocalDateTime.now()).append("] ")
                  .append(request.getAffectedRows()).append(" rows affected\n");
            result.append("[").append(LocalDateTime.now()).append("] Execution completed successfully\n");
            result.append("[").append(LocalDateTime.now()).append("] Verification: No issues found\n");
        } else {
            result.append("[").append(LocalDateTime.now()).append("] Processing ")
                  .append(request.getType().toString().toLowerCase().replace("_", " ")).append("...\n");
            result.append("[").append(LocalDateTime.now()).append("] Changes applied successfully\n");
            result.append("[").append(LocalDateTime.now()).append("] Verification completed\n");
        }
        
        return result.toString();
    }
    
    private String formatValidationResult(SqlValidationService.SqlValidationResult result) {
        StringBuilder formatted = new StringBuilder();
        formatted.append("Validation Status: ").append(result.isValid() ? "PASSED" : "FAILED").append("\n");
        formatted.append("Message: ").append(result.getMessage()).append("\n");
        formatted.append("Statement Type: ").append(result.getStatementType()).append("\n");
        formatted.append("Affected Rows: ").append(result.getAffectedRows()).append("\n");
        formatted.append("Estimated Execution Time: ").append(result.getEstimatedExecutionTime()).append(" seconds\n");
        formatted.append("Impact Level: ").append(result.getImpactLevel()).append("\n");
        formatted.append("Lock Duration: ").append(result.getLockDuration()).append("\n");
        
        if (!result.getAffectedTables().isEmpty()) {
            formatted.append("Affected Tables: ").append(String.join(", ", result.getAffectedTables())).append("\n");
        }
        
        return formatted.toString();
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
    
    public Long getRequestCountByStatus(MaintenanceRequest.RequestStatus status) {
        return requestRepository.countByStatus(status);
    }
    
    public Long getRequestCountByTypeThisMonth(MaintenanceRequest.RequestType type) {
        return requestRepository.countByTypeThisMonth(type);
    }
}
