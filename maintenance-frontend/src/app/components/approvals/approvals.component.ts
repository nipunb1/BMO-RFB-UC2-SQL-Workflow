import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-approvals',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './approvals.component.html',
  styleUrl: './approvals.component.css'
})
export class ApprovalsComponent implements OnInit {
  pendingApprovals = [
    {
      id: 1,
      title: 'Update user permissions table',
      type: 'SQL_FIX',
      submitter: 'John Doe',
      createdAt: '2024-07-19T10:30:00',
      priority: 'HIGH',
      environment: 'PRODUCTION',
      peerReviewer: 'Jane Smith',
      peerReviewStatus: 'APPROVED',
      businessJustification: 'Users need access to new reporting features to complete their daily tasks',
      sqlStatement: `UPDATE user_permissions 
SET can_access_reports = true 
WHERE user_role IN ('manager', 'analyst', 'admin')
AND department = 'finance';`,
      affectedRows: 150
    },
    {
      id: 5,
      title: 'Deploy critical security patch',
      type: 'PATCH_DEPLOYMENT',
      submitter: 'Sarah Wilson',
      createdAt: '2024-07-19T09:45:00',
      priority: 'CRITICAL',
      environment: 'PRODUCTION',
      peerReviewer: null,
      peerReviewStatus: null,
      businessJustification: 'Critical security vulnerability needs immediate patching'
    }
  ];

  ngOnInit() {
  }

  approveRequest(requestId: number) {
    console.log('Approving request:', requestId);
  }

  rejectRequest(requestId: number) {
    console.log('Rejecting request:', requestId);
  }

  requestMoreInfo(requestId: number) {
    console.log('Requesting more info for:', requestId);
  }

  getStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'APPROVED': 'status-approved',
      'REJECTED': 'status-rejected',
      'PENDING': 'status-pending'
    };
    return `status-badge ${statusMap[status] || 'status-pending'}`;
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
    return new Date(dateString).toLocaleDateString();
  }
}
