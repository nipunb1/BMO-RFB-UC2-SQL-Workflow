import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  stats = {
    totalRequests: 24,
    pendingApproval: 8,
    inProgress: 3,
    completed: 13
  };

  recentRequests = [
    {
      id: 1,
      title: 'Update user permissions table',
      type: 'SQL_FIX',
      status: 'PENDING_APPROVAL',
      submitter: 'John Doe',
      createdAt: '2024-07-19T10:30:00'
    },
    {
      id: 2,
      title: 'Rotate application logs',
      type: 'LOG_ROTATION',
      status: 'IN_PROGRESS',
      submitter: 'Jane Smith',
      createdAt: '2024-07-19T09:15:00'
    },
    {
      id: 3,
      title: 'Deploy security patch',
      type: 'PATCH_DEPLOYMENT',
      status: 'COMPLETED',
      submitter: 'Mike Johnson',
      createdAt: '2024-07-19T08:45:00'
    }
  ];

  ngOnInit() {
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

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }
}
