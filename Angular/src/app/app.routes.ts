import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { TaskListComponent } from './components/task-list/task-list.component';
import { ProjectListComponent } from './components/project-list/project-list.component';
import { ProjectDetailComponent } from './components/project-detail/project-detail.component';
import { ManageUsersComponent } from './components/manage-users/manage-users.component';
import { NotFoundComponent } from './components/not-found/not-found.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard]
  },
  {
    path: 'tasks',
    component: TaskListComponent,
    canActivate: [authGuard]
  },
  {
    path: 'projects',
    component: ProjectListComponent,
    canActivate: [authGuard]
  },
  {
    path: 'projects/:id',
    component: ProjectDetailComponent,
    canActivate: [authGuard]
  },
  {
    path: 'team/users',
    component: ManageUsersComponent,
    canActivate: [authGuard]
  },
  { path: '**', component: NotFoundComponent }
];
