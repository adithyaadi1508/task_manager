import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ProjectService } from '../../services/project.service';
import { AuthService } from '../../services/auth.service';
import { Task } from '../../models/task.model';
import { ProjectFormComponent } from '../project-form/project-form.component';
import { TaskFormComponent } from '../task-form/task-form.component';
import { Project, ProjectStats } from '../../models/project.model';
import { TeamManagementComponent } from '../team-management/team-management.component';
import { TaskService } from '../../services/task.service';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-project-detail',
  standalone: true,
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatCardModule,
    MatIconModule,
    MatTabsModule,
    MatTableModule,
    MatChipsModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDialogModule,
    NavbarComponent
  ],
  templateUrl: './project-detail.component.html',
  styleUrl: './project-detail.component.css'
})
export class ProjectDetailComponent implements OnInit {
  projectId!: number;
  project: Project;
  projectStats!: ProjectStats;
  tasks: Task[] = [];
  teamMembers: any[] = [];
  loading: boolean = true;

  taskColumns: string[] = ['title', 'status', 'priority', 'assignedTo', 'dueDate', 'actions'];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private projectService: ProjectService,
    private authService: AuthService,
    private snackBar: MatSnackBar,
    private dialog: MatDialog,
    private taskService: TaskService,
  ) { }

  ngOnInit(): void {
    this.projectId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadProjectDetails();
  }

  loadProjectDetails(): void {
    this.loading = true;
    this.projectService.getProjectById(this.projectId).subscribe({
      next: (project) => {
        this.project = project;
        this.loadProjectStats();
        this.loadProjectTasks();
        this.loadProjectTeam();
      },
      error: (error) => {
        this.snackBar.open('Error loading project', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  loadProjectStats(): void {
    this.projectService.getProjectStats(this.projectId).subscribe({
      next: (stats) => {
        this.projectStats = stats;
      },
      error: (error) => {
        console.error('Error loading project stats', error);
      }
    });
  }

  loadProjectTasks(): void {
    this.projectService.getProjectTasks(this.projectId).subscribe({
      next: (tasks) => {
        this.tasks = tasks;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading tasks', error);
        this.loading = false;
      }
    });
  }

  loadProjectTeam(): void {
    this.projectService.getProjectTeam(this.projectId).subscribe({
      next: (team) => {
        this.teamMembers = team;
      },
      error: (error) => {
        console.error('Error loading team', error);
      }
    });
  }

  openEditDialog(): void {
    const dialogRef = this.dialog.open(ProjectFormComponent, {
      width: '700px',
      data: this.project
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProjectDetails();
      }
    });
  }

  openTaskDialog(task?: Task): void {
    const dialogRef = this.dialog.open(TaskFormComponent, {
      width: '600px',
      data: task ? { ...task, projectId: this.projectId } : { projectId: this.projectId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProjectTasks();
      }
    });
  }

  deleteTask(taskId: number): void {
    if (confirm('Are you sure you want to delete this task?')) {
      this.taskService.deleteTask(taskId).subscribe({
        next: () => {
          this.snackBar.open('Task deleted successfully', 'Close', { duration: 3000 });
          this.loadProjectTasks();
        },
        error: (error) => {
          this.snackBar.open('Error deleting task', 'Close', { duration: 3000 });
        }
      });
    }
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'PLANNING': return '#9e9e9e';
      case 'ACTIVE': return '#4caf50';
      case 'ON_HOLD': return '#ff9800';
      case 'COMPLETED': return '#2196f3';
      case 'CANCELLED': return '#f44336';
      case 'TODO': return '#ff9800';
      case 'IN_PROGRESS': return '#2196f3';
      case 'IN_REVIEW': return '#9c27b0';
      case 'BLOCKED': return '#f44336';
      default: return '';
    }
  }

  getPriorityColor(priority: string): string {
    switch (priority) {
      case 'CRITICAL': return '#d32f2f';
      case 'HIGH': return '#f44336';
      case 'MEDIUM': return '#ff9800';
      case 'LOW': return '#4caf50';
      default: return '';
    }
  }

  goBack(): void {
    this.router.navigate(['/projects']);
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
  openTeamManagement(): void {
    const dialogRef = this.dialog.open(TeamManagementComponent, {
      width: '600px',
      data: { projectId: this.projectId }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadProjectTeam();
      }
    });
  }
}
