import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../services/auth.service';
import { TaskService } from '../../services/task.service';
import { LoadingSpinnerComponent } from '../shared/loading-spinner/loading-spinner.component';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    LoadingSpinnerComponent,
    NavbarComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  username: string = '';
  loading: boolean = true;
  taskStats = {
    total: 0,
    pending: 0,
    inProgress: 0,
    completed: 0
  };

  constructor(
    private authService: AuthService,
    private taskService: TaskService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.username = this.authService.getUsername() || 'User';
    this.loadTaskStats();
  }

  loadTaskStats(): void {
    this.taskService.getAllTasks().subscribe({
      next: (tasks) => {
        this.taskStats.total = tasks.length;
        this.taskStats.pending = tasks.filter(t => t.status === 'TODO').length;
        this.taskStats.inProgress = tasks.filter(t => t.status === 'IN_PROGRESS').length;
        this.taskStats.completed = tasks.filter(t => t.status === 'COMPLETED').length;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading tasks', error);
        this.loading = false;
      }
    });
  }

  navigateToProjects(): void {
    this.router.navigate(['/projects']);
  }

  navigateToTasks(): void {
    this.router.navigate(['/tasks']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
