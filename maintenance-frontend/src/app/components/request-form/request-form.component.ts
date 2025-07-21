import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-request-form',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './request-form.component.html',
  styleUrl: './request-form.component.css'
})
export class RequestFormComponent implements OnInit {
  request = {
    title: '',
    description: '',
    type: 'SQL_FIX',
    priority: 'MEDIUM',
    environment: 'PRODUCTION',
    application: '',
    businessJustification: '',
    rollbackPlan: '',
    sqlStatement: '',
    configContent: '',
    jobDetails: '',
    peerReviewer: ''
  };

  validationResult: any = null;
  isValidating = false;
  showValidation = false;

  constructor(private router: Router) {}

  ngOnInit() {
  }

  onTypeChange() {
    this.request.sqlStatement = '';
    this.request.configContent = '';
    this.request.jobDetails = '';
    this.validationResult = null;
    this.showValidation = false;
  }

  validateSql() {
    if (!this.request.sqlStatement.trim()) {
      return;
    }

    this.isValidating = true;
    
    setTimeout(() => {
      this.validationResult = {
        isValid: true,
        message: 'SQL statement is valid',
        statementType: 'UPDATE',
        affectedRows: 150,
        estimatedExecutionTime: 2.5,
        impactLevel: 'MEDIUM',
        lockDuration: '< 1 second',
        affectedTables: ['users', 'user_permissions']
      };
      this.showValidation = true;
      this.isValidating = false;
    }, 1500);
  }

  onSubmit() {
    if (this.request.type === 'SQL_FIX' && !this.validationResult?.isValid) {
      alert('Please validate SQL statement before submitting');
      return;
    }

    console.log('Submitting request:', this.request);
    
    this.router.navigate(['/requests']);
  }

  onSaveDraft() {
    console.log('Saving draft:', this.request);
    this.router.navigate(['/requests']);
  }
}
