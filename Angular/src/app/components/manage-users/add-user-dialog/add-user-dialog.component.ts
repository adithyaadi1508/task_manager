import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../services/auth.service';

@Component({
    selector: 'app-add-user-dialog',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatDialogModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        MatSnackBarModule
    ],
    templateUrl: './add-user-dialog.component.html',
    styleUrl: './add-user-dialog.component.css'
})
export class AddUserDialogComponent {
    registerForm: FormGroup;
    isSubmitting = false;

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private dialogRef: MatDialogRef<AddUserDialogComponent>,
        private snackBar: MatSnackBar
    ) {
        this.registerForm = this.fb.group({
            username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
            email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
            password: ['', [Validators.required, Validators.minLength(6)]],
            firstName: ['', [Validators.required, Validators.maxLength(50)]],
            lastName: ['', [Validators.required, Validators.maxLength(50)]],
            phone: ['', [Validators.maxLength(20)]]
        });
    }

    onSubmit(): void {
        if (this.registerForm.valid && !this.isSubmitting) {
            this.isSubmitting = true;
            this.authService.register(this.registerForm.value).subscribe({
                next: (response) => {
                    this.snackBar.open('User added successfully!', 'Close', { duration: 3000 });
                    this.dialogRef.close('success');
                },
                error: (error) => {
                    const errorMsg = error.error?.message || 'Failed to add user. Try again.';
                    this.snackBar.open(errorMsg, 'Close', { duration: 3000 });
                    this.isSubmitting = false;
                }
            });
        }
    }

    onCancel(): void {
        this.dialogRef.close();
    }
}
