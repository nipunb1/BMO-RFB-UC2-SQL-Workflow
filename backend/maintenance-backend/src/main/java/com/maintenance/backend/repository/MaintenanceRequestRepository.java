package com.maintenance.backend.repository;

import com.maintenance.backend.model.MaintenanceRequest;
import com.maintenance.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findBySubmitter(User submitter);
    List<MaintenanceRequest> findByStatus(MaintenanceRequest.RequestStatus status);
    List<MaintenanceRequest> findByType(MaintenanceRequest.RequestType type);
    
    @Query("SELECT r FROM MaintenanceRequest r WHERE r.status = 'PENDING_APPROVAL'")
    List<MaintenanceRequest> findPendingApprovalRequests();
    
    @Query("SELECT COUNT(r) FROM MaintenanceRequest r WHERE r.status = ?1")
    Long countByStatus(MaintenanceRequest.RequestStatus status);
    
    @Query("SELECT COUNT(r) FROM MaintenanceRequest r WHERE r.type = ?1 AND MONTH(r.createdAt) = MONTH(CURRENT_DATE) AND YEAR(r.createdAt) = YEAR(CURRENT_DATE)")
    Long countByTypeThisMonth(MaintenanceRequest.RequestType type);
}
