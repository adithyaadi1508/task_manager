import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Role } from '../../../models/user.model';

@Component({
    selector: 'app-role-dialog',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule
    ],
    templateUrl: './role-dialog.component.html',
    styleUrl: './role-dialog.component.css'
})
export class RoleDialogComponent {
    roleForm: FormGroup;
    isEditMode: boolean;

    constructor(
        private fb: FormBuilder,
        public dialogRef: MatDialogRef<RoleDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: Role | null
    ) {
        this.isEditMode = !!data;
        this.roleForm = this.fb.group({
            name: [data?.name || '', [Validators.required, Validators.minLength(2)]],
            description: [data?.description || '', [Validators.required, Validators.minLength(5)]]
        });
    }

    onCancel(): void {
        this.dialogRef.close();
    }

    onSave(): void {
        if (this.roleForm.valid) {
            const result = {
                ...this.data,
                ...this.roleForm.value
            };
            this.dialogRef.close(result);
        }
    }

    getErrorMessage(field: string): string {
        const control = this.roleForm.get(field);
        if (control?.hasError('required')) {
            return `${field.charAt(0).toUpperCase() + field.slice(1)} is required`;
        }
        if (control?.hasError('minlength')) {
            const minLength = control.errors?.['minlength'].requiredLength;
            return `${field.charAt(0).toUpperCase() + field.slice(1)} must be at least ${minLength} characters`;
        }
        return '';
    }
}
