package com.maintenance.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "maintenance_requests")
public class MaintenanceRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(max = 200)
    private String title;
    
    @Enumerated(EnumType.STRING)
    @NotNull
    private RequestType type;
    
    @Enumerated(EnumType.STRING)
    private RequestStatus status;
    
    @Enumerated(EnumType.STRING)
    private Priority priority;
    
    @NotBlank
    @Size(max = 100)
    private String application;
    
    @Enumerated(EnumType.STRING)
    private Environment environment;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "business_justification", columnDefinition = "TEXT")
    private String businessJustification;
    
    @Column(name = "rollback_plan", columnDefinition = "TEXT")
    private String rollbackPlan;
    
    @Column(name = "sql_statement", columnDefinition = "TEXT")
    private String sqlStatement;
    
    @Column(name = "config_content", columnDefinition = "TEXT")
    private String configContent;
    
    @Column(name = "job_details", columnDefinition = "TEXT")
    private String jobDetails;
    
    @Column(name = "validation_result", columnDefinition = "TEXT")
    private String validationResult;
    
    @Column(name = "execution_result", columnDefinition = "TEXT")
    private String executionResult;
    
    @Column(name = "affected_rows")
    private Integer affectedRows;
    
    @Column(name = "execution_time")
    private Double executionTime;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitter_id")
    private User submitter;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "peer_reviewer_id")
    private User peerReviewer;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    private List<ApprovalWorkflow> approvals;
    
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    private List<AuditLog> auditLogs;
    
    public enum RequestType {
        SQL_FIX, CONFIG_UPDATE, JOB_CONTROL, LOG_MANAGEMENT, JOB_TRIGGER, PATCH_DEPLOYMENT
    }
    
    public enum RequestStatus {
        DRAFT, SUBMITTED, PEER_REVIEW, PENDING_APPROVAL, APPROVED, REJECTED, IN_PROGRESS, COMPLETED, FAILED, CANCELLED
    }
    
    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    public enum Environment {
        DEVELOPMENT, TESTING, STAGING, PRODUCTION
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = RequestStatus.DRAFT;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBusinessJustification() {
        return businessJustification;
    }

    public void setBusinessJustification(String businessJustification) {
        this.businessJustification = businessJustification;
    }

    public String getRollbackPlan() {
        return rollbackPlan;
    }

    public void setRollbackPlan(String rollbackPlan) {
        this.rollbackPlan = rollbackPlan;
    }

    public String getSqlStatement() {
        return sqlStatement;
    }

    public void setSqlStatement(String sqlStatement) {
        this.sqlStatement = sqlStatement;
    }

    public String getConfigContent() {
        return configContent;
    }

    public void setConfigContent(String configContent) {
        this.configContent = configContent;
    }

    public String getJobDetails() {
        return jobDetails;
    }

    public void setJobDetails(String jobDetails) {
        this.jobDetails = jobDetails;
    }

    public String getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(String validationResult) {
        this.validationResult = validationResult;
    }

    public String getExecutionResult() {
        return executionResult;
    }

    public void setExecutionResult(String executionResult) {
        this.executionResult = executionResult;
    }

    public Integer getAffectedRows() {
        return affectedRows;
    }

    public void setAffectedRows(Integer affectedRows) {
        this.affectedRows = affectedRows;
    }

    public Double getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Double executionTime) {
        this.executionTime = executionTime;
    }

    public User getSubmitter() {
        return submitter;
    }

    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }

    public User getPeerReviewer() {
        return peerReviewer;
    }

    public void setPeerReviewer(User peerReviewer) {
        this.peerReviewer = peerReviewer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public List<ApprovalWorkflow> getApprovals() {
        return approvals;
    }

    public void setApprovals(List<ApprovalWorkflow> approvals) {
        this.approvals = approvals;
    }

    public List<AuditLog> getAuditLogs() {
        return auditLogs;
    }

    public void setAuditLogs(List<AuditLog> auditLogs) {
        this.auditLogs = auditLogs;
    }
}
