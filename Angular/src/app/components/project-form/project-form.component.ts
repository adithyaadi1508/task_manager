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
import { MatSliderModule } from '@angular/material/slider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProjectService } from '../../services/project.service';
import { Project } from '../../models/project.model';

@Component({
  selector: 'app-project-form',
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
    MatSliderModule,
    MatSnackBarModule
  ],
  templateUrl: './project-form.component.html',
  styleUrl: './project-form.component.css'
})
export class ProjectFormComponent implements OnInit {
  projectForm: FormGroup;
  isEditMode: boolean = false;

  statuses = ['PLANNING', 'ACTIVE', 'ON_HOLD', 'COMPLETED', 'CANCELLED'];
  priorities = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];

  constructor(
    private fb: FormBuilder,
    private projectService: ProjectService,
    private snackBar: MatSnackBar,
    private dialogRef: MatDialogRef<ProjectFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Project | null
  ) {
    this.projectForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      description: ['', [Validators.required, Validators.maxLength(500)]],
      status: ['PLANNING', [Validators.required]],
      priority: ['MEDIUM', [Validators.required]],
      startDate: ['', [Validators.required]],
      endDate: [''],
      budget: [0],
      progress: [0, [Validators.min(0), Validators.max(100)]]
    });
  }

  ngOnInit(): void {
    if (this.data) {
      this.isEditMode = true;
      this.projectForm.patchValue({
        name: this.data.name,
        description: this.data.description,
        status: this.data.status,
        priority: this.data.priority,
        startDate: new Date(this.data.startDate),
        endDate: this.data.endDate ? new Date(this.data.endDate) : null,
        budget: this.data.budget || 0,
        progress: this.data.progress || 0
      });
    }
  }

  onSubmit(): void {
    if (this.projectForm.valid) {
      const projectData: Project = {
        ...this.projectForm.value,
        startDate: this.formatDate(this.projectForm.value.startDate),
        endDate: this.projectForm.value.endDate ? this.formatDate(this.projectForm.value.endDate) : null
      };

      if (this.isEditMode && this.data?.id) {
        this.projectService.updateProject(this.data.id, projectData).subscribe({
          next: () => {
            this.snackBar.open('Project updated successfully', 'Close', { duration: 3000 });
            this.dialogRef.close(true);
          },
          error: (error) => {
            const errorMsg = error.error?.message || 'Error updating project';
            this.snackBar.open(errorMsg, 'Close', { duration: 3000 });
          }
        });
      } else {
        this.projectService.createProject(projectData).subscribe({
          next: () => {
            this.snackBar.open('Project created successfully', 'Close', { duration: 3000 });
            this.dialogRef.close(true);
          },
          error: (error) => {
            const errorMsg = error.error?.message || 'Error creating project';
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
  formatLabel(value: number): string {
  return `${value}%`;
}
}
