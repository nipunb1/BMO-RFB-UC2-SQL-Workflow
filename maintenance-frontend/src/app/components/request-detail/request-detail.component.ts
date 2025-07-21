import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-request-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './request-detail.component.html',
  styleUrl: './request-detail.component.css'
})
export class RequestDetailComponent implements OnInit {
  requestId: string | null = null;
  request: any = null;
  
  timeline = [
    {
      action: 'Request Created',
      user: 'John Doe',
      timestamp: '2024-07-19T10:30:00',
      details: 'Initial request submitted for review'
    },
    {
      action: 'SQL Validation',
      user: 'System',
      timestamp: '2024-07-19T10:31:00',
      details: 'SQL statement validated successfully - 150 rows affected'
    },
    {
      action: 'Peer Review Assigned',
      user: 'John Doe',
      timestamp: '2024-07-19T10:32:00',
      details: 'Assigned to Jane Smith for peer review'
    },
    {
      action: 'Peer Review Completed',
      user: 'Jane Smith',
      timestamp: '2024-07-19T11:15:00',
      details: 'Peer review approved - SQL looks good'
    },
    {
      action: 'Manager Approval Requested',
      user: 'System',
      timestamp: '2024-07-19T11:16:00',
      details: 'Sent to Mike Johnson for final approval'
    }
  ];

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    this.requestId = this.route.snapshot.paramMap.get('id');
    this.loadRequest();
  }

  loadRequest() {
    this.request = {
      id: this.requestId,
      title: 'Update user permissions table',
      description: 'Need to update user permissions to grant access to new reporting module',
      type: 'SQL_FIX',
      status: 'PENDING_APPROVAL',
      priority: 'HIGH',
      environment: 'PRODUCTION',
      application: 'User Management System',
      submitter: 'John Doe',
      peerReviewer: 'Jane Smith',
      createdAt: '2024-07-19T10:30:00',
      businessJustification: 'Users need access to new reporting features to complete their daily tasks',
      rollbackPlan: 'Revert permissions using backup script if issues occur',
      sqlStatement: `UPDATE user_permissions 
SET can_access_reports = true 
WHERE user_role IN ('manager', 'analyst', 'admin')
AND department = 'finance';`,
      validationResult: {
        isValid: true,
        message: 'SQL statement is valid',
        statementType: 'UPDATE',
        affectedRows: 150,
        estimatedExecutionTime: 2.5,
        impactLevel: 'MEDIUM',
        lockDuration: '< 1 second',
        affectedTables: ['user_permissions']
      }
    };
  }

  getStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING_APPROVAL': 'status-pending',
      'IN_PROGRESS': 'status-in-progress',
      'COMPLETED': 'status-completed',
      'REJECTED': 'status-rejected',
      'DRAFT': 'status-draft'
    };
    return `status-badge ${statusMap[status] || 'status-draft'}`;
  }

  getPriorityClass(priority: string): string {
    const priorityMap: { [key: string]: string } = {
      'HIGH': 'priority-high',
      'MEDIUM': 'priority-medium',
      'LOW': 'priority-low',
      'CRITICAL': 'priority-critical'
    };
    return `priority-badge ${priorityMap[priority] || 'priority-medium'}`;
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleString();
  }
}
