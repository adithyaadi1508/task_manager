import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { forkJoin } from 'rxjs';
import { TaskService } from '../../services/task.service';
import { ProjectService } from '../../services/project.service';
import { UserService } from '../../services/user.service';
import { Task } from '../../models/task.model';
import { UserSimple } from '../../models/user.model';
import { Project } from '../../models/project.model';

@Component({
  selector: 'app-task-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatSnackBarModule
  ],
  templateUrl: './task-form.component.html',
  styleUrl: './task-form.component.css'
})
export class TaskFormComponent implements OnInit {
  taskForm: FormGroup;
  isEditMode: boolean = false;
  
  statuses = ['TODO', 'IN_PROGRESS', 'IN_REVIEW', 'BLOCKED', 'COMPLETED'];
  priorities = ['LOW', 'MEDIUM', 'HIGH'];
  
  projects: Project[] = [];
  users: UserSimple[] = [];
  loading: boolean = true;

  constructor(
    private fb: FormBuilder,
    private taskService: TaskService,
    private projectService: ProjectService,
    private userService: UserService,
    private snackBar: MatSnackBar,
    private dialogRef: MatDialogRef<TaskFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Task | null
  ) {
    this.taskForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required]],
      status: ['TODO', [Validators.required]],
      priority: ['MEDIUM', [Validators.required]],
      dueDate: ['', [Validators.required]],
      projectId: ['', [Validators.required]],
      assignedToId: [null]  // Optional
    });
  }

  ngOnInit(): void {
    this.loadDropdownData();
  }

  loadDropdownData(): void {
    forkJoin({
      projects: this.projectService.getAllProjects(),
      users: this.userService.getAllUsers()
    }).subscribe({
      next: (result) => {
        this.projects = result.projects;
        this.users = result.users;
        this.loading = false;
        
        // Set default project if available
        if (this.projects.length > 0 && !this.data) {
          this.taskForm.patchValue({ projectId: this.projects[0].id });
        }
        
        // Populate form if editing
        if (this.data) {
          this.isEditMode = true;
          this.taskForm.patchValue({
            title: this.data.title,
            description: this.data.description,
            status: this.data.status,
            priority: this.data.priority,
            dueDate: new Date(this.data.dueDate),
            projectId: this.data.project?.id,
            assignedToId: this.data.assignedTo?.id
          });
        }
      },
      error: (error) => {
        console.error('Error loading dropdown data', error);
        this.snackBar.open('Error loading projects/users', 'Close', { duration: 3000 });
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.taskForm.valid) {
      const taskData: Task = {
        ...this.taskForm.value,
        dueDate: this.formatDate(this.taskForm.value.dueDate)
      };

      if (this.isEditMode && this.data?.id) {
        this.taskService.updateTask(this.data.id, taskData).subscribe({
          next: () => {
            this.snackBar.open('Task updated successfully', 'Close', { duration: 3000 });
            this.dialogRef.close(true);
          },
          error: (error) => {
            const errorMsg = error.error?.message || 'Error updating task';
            this.snackBar.open(errorMsg, 'Close', { duration: 3000 });
          }
        });
      } else {
        this.taskService.createTask(taskData).subscribe({
          next: () => {
            this.snackBar.open('Task created successfully', 'Close', { duration: 3000 });
            this.dialogRef.close(true);
          },
          error: (error) => {
            const errorMsg = error.error?.message || 'Error creating task';
            this.snackBar.open(errorMsg, 'Close', { duration: 3000 });
          }
        });
      }
    }
  }

  formatDate(date: Date): string {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  onCancel(): void {
    this.dialogRef.close(false);
  }
}
