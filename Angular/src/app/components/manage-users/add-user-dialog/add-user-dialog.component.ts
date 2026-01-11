import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogRef, MatDialogModule, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../../services/auth.service';
import { UserService } from '../../../services/user.service';
import { UserSimple, Role } from '../../../models/user.model';
import { RoleService } from '../../../services/role.service';
import { MatSelectModule } from '@angular/material/select';

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
        MatSnackBarModule,
        MatSelectModule
    ],
    templateUrl: './add-user-dialog.component.html',
    styleUrl: './add-user-dialog.component.css'
})
export class AddUserDialogComponent {
    registerForm: FormGroup;
    isSubmitting = false;
    isEditMode = false;
    userId?: number;
    roles: Role[] = [];

    constructor(
        private fb: FormBuilder,
        private authService: AuthService,
        private userService: UserService,
        private roleService: RoleService,
        private dialogRef: MatDialogRef<AddUserDialogComponent>,
        private snackBar: MatSnackBar,
        @Inject(MAT_DIALOG_DATA) public data: { mode: string; user?: UserSimple }
    ) {
        this.isEditMode = data?.mode === 'edit';
        this.userId = data?.user?.id;

        this.registerForm = this.fb.group({
            username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
            email: ['', [Validators.required, Validators.email, Validators.maxLength(100)]],
            password: ['', this.isEditMode ? [] : [Validators.required, Validators.minLength(6)]],
            firstName: ['', [Validators.required, Validators.maxLength(50)]],
            lastName: ['', [Validators.required, Validators.maxLength(50)]],
            phone: ['', [Validators.maxLength(20)]],
            roles: [[], [Validators.required]]
        });

        this.loadRoles();

        // Pre-fill form if in edit mode
        if (this.isEditMode && data.user) {
            this.registerForm.patchValue({
                username: data.user.username,
                email: data.user.email,
                firstName: data.user.firstName,
                lastName: data.user.lastName,
                phone: data.user.phone || '',
                roles: data.user.roles || []
            });
        }
    }

    loadRoles() {
        this.roleService.getAllRoles().subscribe({
            next: (roles) => {

                this.roles = roles;
            },
            error: (error) => {
                console.error('Error fetching roles:', error);
                this.snackBar.open('Failed to load roles', 'Close', { duration: 3000 });
            }
        });
    }

    onSubmit(): void {
        if (this.registerForm.valid && !this.isSubmitting) {
            this.isSubmitting = true;

            if (this.isEditMode && this.userId) {
                // Update existing user
                const updateData = { ...this.registerForm.value };
                // Remove password if not provided in edit mode

                if (!updateData.password) {
                    delete updateData.password;
                }
                this.userService.updateUser(this.userId, updateData).subscribe({
                    next: (response) => {
                        this.snackBar.open('User updated successfully!', 'Close', { duration: 3000 });
                        this.dialogRef.close('success');
                    },
                    error: (error) => {
                        const errorMsg = error.error?.message || 'Failed to update user. Try again.';
                        this.snackBar.open(errorMsg, 'Close', { duration: 3000 });
                        this.isSubmitting = false;
                    }
                });
            } else {
                // Add new user
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
    }

    onCancel(): void {
        this.dialogRef.close();
    }
}
