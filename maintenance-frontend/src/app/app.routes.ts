import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: 'dashboard', loadComponent: () => import('./components/dashboard/dashboard.component').then(m => m.DashboardComponent) },
  { path: 'requests', loadComponent: () => import('./components/requests/requests.component').then(m => m.RequestsComponent) },
  { path: 'requests/new', loadComponent: () => import('./components/request-form/request-form.component').then(m => m.RequestFormComponent) },
  { path: 'requests/:id', loadComponent: () => import('./components/request-detail/request-detail.component').then(m => m.RequestDetailComponent) },
  { path: 'approvals', loadComponent: () => import('./components/approvals/approvals.component').then(m => m.ApprovalsComponent) },
  { path: 'admin', loadComponent: () => import('./components/admin/admin.component').then(m => m.AdminComponent) },
  { path: '**', redirectTo: '/dashboard' }
];
