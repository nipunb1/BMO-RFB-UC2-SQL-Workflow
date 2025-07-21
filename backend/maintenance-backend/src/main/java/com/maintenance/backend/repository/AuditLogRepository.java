package com.maintenance.backend.repository;

import com.maintenance.backend.model.AuditLog;
import com.maintenance.backend.model.MaintenanceRequest;
import com.maintenance.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByRequest(MaintenanceRequest request);
    List<AuditLog> findByUser(User user);
    List<AuditLog> findByAction(AuditLog.ActionType action);
    List<AuditLog> findByRequestOrderByCreatedAtDesc(MaintenanceRequest request);
}
