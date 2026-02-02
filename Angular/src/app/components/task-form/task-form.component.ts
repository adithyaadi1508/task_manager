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
import { TaskAttachment, TaskAttachmentService } from '../../services/task-attachment.service';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';

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
    MatSnackBarModule,
    MatIconModule,
    MatListModule,
    MatProgressSpinnerModule
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

  // File attachment properties
  selectedFile: File | null = null;
  attachments: TaskAttachment[] = [];
  isUploading: boolean = false;
  loadingAttachments: boolean = false;

   // File validation
  allowedFileTypes = ['.pdf', '.doc', '.docx', '.xlsx', '.xls', '.jpg', '.jpeg', '.png'];
  maxFileSize = 10 * 1024 * 1024; // 10MB


  constructor(
    private fb: FormBuilder,
    private taskService: TaskService,
    private projectService: ProjectService,
    private userService: UserService,
    private snackBar: MatSnackBar,
    private attachmentService: TaskAttachmentService,
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

       if (this.data.id) {
          this.loadAttachments(this.data.id);
        }
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
      this.updateTask(this.data.id, taskData);
    } else {
      this.createTask(taskData);
    }
  }
}

private createTask(taskData: Task): void {
  this.taskService.createTask(taskData).subscribe({
    next: (createdTask) => {
      // Check if there's a file to upload
      if (this.selectedFile && createdTask.id) {
        this.uploadAttachment(createdTask.id);
      } else {
        this.snackBar.open('Task created successfully', 'Close', { duration: 3000 });
        this.dialogRef.close(true);
      }
    },
    error: (error) => {
      const errorMsg = error.error?.message || 'Error creating task';
      this.snackBar.open(errorMsg, 'Close', { duration: 3000 });
    }
  });
}

private updateTask(taskId: number, taskData: Task): void {
  this.taskService.updateTask(taskId, taskData).subscribe({
    next: () => {
      // Check if there's a file to upload
      if (this.selectedFile) {
        this.uploadAttachment(taskId);
      } else {
        this.snackBar.open('Task updated successfully', 'Close', { duration: 3000 });
        this.dialogRef.close(true);
      }
    },
    error: (error) => {
      const errorMsg = error.error?.message || 'Error updating task';
      this.snackBar.open(errorMsg, 'Close', { duration: 3000 });
    }
  });
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
  // File handling methods
  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    
    if (file) {
      // Validate file size
      if (file.size > this.maxFileSize) {
        this.snackBar.open('File size must be less than 10MB', 'Close', { duration: 3000 });
        event.target.value = ''; // Reset file input
        return;
      }

      // Validate file type
      const fileExtension = '.' + file.name.split('.').pop()?.toLowerCase();
      if (!this.allowedFileTypes.includes(fileExtension)) {
        this.snackBar.open('Invalid file type. Allowed: PDF, DOC, XLSX, Images', 'Close', { duration: 3000 });
        event.target.value = ''; // Reset file input
        return;
      }

      this.selectedFile = file;
    }
  }

  removeSelectedFile(): void {
    this.selectedFile = null;
  }

  private loadAttachments(taskId: number): void {
    this.loadingAttachments = true;
    this.attachmentService.getTaskAttachments(taskId).subscribe({
      next: (attachments) => {
        this.attachments = attachments;
        this.loadingAttachments = false;
      },
      error: (error) => {
        console.error('Error loading attachments', error);
        this.loadingAttachments = false;
      }
    });
  }

  downloadAttachment(attachment: TaskAttachment): void {
    this.attachmentService.downloadAttachment(attachment.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = attachment.fileName;
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        this.snackBar.open('Error downloading file', 'Close', { duration: 3000 });
      }
    });
  }

  deleteAttachment(attachment: TaskAttachment): void {
    if (confirm(`Are you sure you want to delete "${attachment.fileName}"?`)) {
      this.attachmentService.deleteAttachment(attachment.id).subscribe({
        next: () => {
          this.attachments = this.attachments.filter(a => a.id !== attachment.id);
          this.snackBar.open('Attachment deleted successfully', 'Close', { duration: 2000 });
        },
        error: (error) => {
          this.snackBar.open('Error deleting attachment', 'Close', { duration: 3000 });
        }
      });
    }
  }

  getFileIcon(fileName: string): string {
    const extension = fileName.split('.').pop()?.toLowerCase();
    switch (extension) {
      case 'pdf': return 'picture_as_pdf';
      case 'doc':
      case 'docx': return 'description';
      case 'xlsx':
      case 'xls': return 'table_chart';
      case 'jpg':
      case 'jpeg':
      case 'png': return 'image';
      default: return 'attach_file';
    }
  }

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
  }
private uploadAttachment(taskId: number): void {
  debugger
    if (!this.selectedFile) return;

    this.isUploading = true;
    this.attachmentService.uploadAttachment(taskId, this.selectedFile).subscribe({
      next: () => {
        this.isUploading = false;
        const message = this.isEditMode ? 
          'Task and attachment saved successfully' : 
          'Task created and attachment uploaded successfully';
        this.snackBar.open(message, 'Close', { duration: 3000 });
        this.dialogRef.close(true);
      },
      error: (error) => {
        this.isUploading = false;
        const message = this.isEditMode ?
          'Task updated but attachment upload failed' :
          'Task created but attachment upload failed';
        this.snackBar.open(message, 'Close', { duration: 3000 });
        this.dialogRef.close(true);
      }
    });
  }
}
