import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-requests',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './requests.component.html',
  styleUrl: './requests.component.css'
})
export class RequestsComponent implements OnInit {
  requests = [
    {
      id: 1,
      title: 'Update user permissions table',
      type: 'SQL_FIX',
      status: 'PENDING_APPROVAL',
      submitter: 'John Doe',
      createdAt: '2024-07-19T10:30:00',
      priority: 'HIGH'
    },
    {
      id: 2,
      title: 'Rotate application logs',
      type: 'LOG_ROTATION',
      status: 'IN_PROGRESS',
      submitter: 'Jane Smith',
      createdAt: '2024-07-19T09:15:00',
      priority: 'MEDIUM'
    },
    {
      id: 3,
      title: 'Deploy security patch',
      type: 'PATCH_DEPLOYMENT',
      status: 'COMPLETED',
      submitter: 'Mike Johnson',
      createdAt: '2024-07-19T08:45:00',
      priority: 'HIGH'
    },
    {
      id: 4,
      title: 'Update configuration file',
      type: 'CONFIG_UPDATE',
      status: 'DRAFT',
      submitter: 'Sarah Wilson',
      createdAt: '2024-07-19T07:30:00',
      priority: 'LOW'
    }
  ];

  filteredRequests = [...this.requests];
  selectedStatus = 'ALL';
  selectedType = 'ALL';

  ngOnInit() {
  }

  filterRequests() {
    this.filteredRequests = this.requests.filter(request => {
      const statusMatch = this.selectedStatus === 'ALL' || request.status === this.selectedStatus;
      const typeMatch = this.selectedType === 'ALL' || request.type === this.selectedType;
      return statusMatch && typeMatch;
    });
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
      'LOW': 'priority-low'
    };
    return `priority-badge ${priorityMap[priority] || 'priority-medium'}`;
  }

  formatDate(dateString: string): string {
    return new Date(dateString).toLocaleDateString();
  }
}
