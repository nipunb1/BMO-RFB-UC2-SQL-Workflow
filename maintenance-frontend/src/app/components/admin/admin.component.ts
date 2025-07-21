import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css'
})
export class AdminComponent implements OnInit {
  activeTab = 'users';
  
  users: Array<{
    id: number;
    name: string;
    email: string;
    role: string;
    status: string;
    lastLogin: string | null;
  }> = [
    {
      id: 1,
      name: 'John Doe',
      email: 'john.doe@company.com',
      role: 'DEVELOPER',
      status: 'ACTIVE',
      lastLogin: '2024-07-19T08:30:00'
    },
    {
      id: 2,
      name: 'Jane Smith',
      email: 'jane.smith@company.com',
      role: 'SENIOR_DEVELOPER',
      status: 'ACTIVE',
      lastLogin: '2024-07-19T09:15:00'
    },
    {
      id: 3,
      name: 'Mike Johnson',
      email: 'mike.johnson@company.com',
      role: 'MANAGER',
      status: 'ACTIVE',
      lastLogin: '2024-07-19T07:45:00'
    },
    {
      id: 4,
      name: 'Sarah Wilson',
      email: 'sarah.wilson@company.com',
      role: 'ADMIN',
      status: 'ACTIVE',
      lastLogin: '2024-07-18T16:20:00'
    }
  ];

  systemStats = {
    totalUsers: 24,
    activeUsers: 22,
    totalRequests: 156,
    completedRequests: 142,
    pendingApprovals: 8,
    systemUptime: '99.9%',
    avgProcessingTime: '2.3 hours',
    successRate: '98.7%'
  };

  auditLogs = [
    {
      id: 1,
      action: 'REQUEST_CREATED',
      user: 'John Doe',
      details: 'Created SQL fix request #123',
      timestamp: '2024-07-19T10:30:00',
      ipAddress: '192.168.1.100'
    },
    {
      id: 2,
      action: 'APPROVAL_GRANTED',
      user: 'Mike Johnson',
      details: 'Approved request #122 for config update',
      timestamp: '2024-07-19T10:15:00',
      ipAddress: '192.168.1.105'
    },
    {
      id: 3,
      action: 'USER_LOGIN',
      user: 'Jane Smith',
      details: 'User logged into system',
      timestamp: '2024-07-19T09:15:00',
      ipAddress: '192.168.1.102'
    }
  ];

  newUser = {
    name: '',
    email: '',
    role: 'DEVELOPER',
    status: 'ACTIVE'
  };

  ngOnInit() {
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }

  createUser() {
    if (this.newUser.name && this.newUser.email) {
      const user = {
        id: this.users.length + 1,
        ...this.newUser,
        lastLogin: null
      };
      this.users.push(user);
      this.resetNewUser();
      console.log('User created:', user);
    }
  }

  updateUserStatus(userId: number, status: string) {
    const user = this.users.find(u => u.id === userId);
    if (user) {
      user.status = status;
      console.log('User status updated:', user);
    }
  }

  updateUserRole(userId: number, role: string) {
    const user = this.users.find(u => u.id === userId);
    if (user) {
      user.role = role;
      console.log('User role updated:', user);
    }
  }

  deleteUser(userId: number) {
    if (confirm('Are you sure you want to delete this user?')) {
      this.users = this.users.filter(u => u.id !== userId);
      console.log('User deleted:', userId);
    }
  }

  resetNewUser() {
    this.newUser = {
      name: '',
      email: '',
      role: 'DEVELOPER',
      status: 'ACTIVE'
    };
  }

  getStatusClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'ACTIVE': 'status-approved',
      'INACTIVE': 'status-rejected',
      'SUSPENDED': 'status-pending'
    };
    return `status-badge ${statusMap[status] || 'status-draft'}`;
  }

  formatDate(dateString: string | null): string {
    if (!dateString) return 'Never';
    return new Date(dateString).toLocaleString();
  }
}
