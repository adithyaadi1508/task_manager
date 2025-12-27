import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { UserService } from '../../services/user.service';
import { UserSimple } from '../../models/user.model';
import { AddUserDialogComponent } from './add-user-dialog/add-user-dialog.component';
import { NavbarComponent } from '../navbar/navbar.component';
import { Router } from '@angular/router';

@Component({
    selector: 'app-manage-users',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatTableModule,
        MatDialogModule,
        MatSnackBarModule,
        NavbarComponent
    ],
    templateUrl: './manage-users.component.html',
    styleUrl: './manage-users.component.css'
})
export class ManageUsersComponent implements OnInit {
    users: UserSimple[] = [];
    displayedColumns: string[] = ['id', 'username', 'firstName', 'lastName', 'email', 'actions'];
    isLoading = true;

    constructor(
        private userService: UserService,
        private dialog: MatDialog,
        private snackBar: MatSnackBar,
        private router: Router,
    ) { }

    ngOnInit(): void {
        this.loadUsers();
    }

    loadUsers(): void {
        this.isLoading = true;
        this.userService.getAllUsers().subscribe({
            next: (users) => {
                this.users = users;
                this.isLoading = false;
            },
            error: (error) => {
                this.snackBar.open('Failed to load users', 'Close', { duration: 3000 });
                this.isLoading = false;
            }
        });
    }

    openAddUserDialog(): void {
        const dialogRef = this.dialog.open(AddUserDialogComponent, {
            width: '600px',
            disableClose: false
        });

        dialogRef.afterClosed().subscribe(result => {
            if (result === 'success') {
                this.loadUsers(); // Reload users after successful addition
            }
        });
    }

    deleteUser(userId: number): void {
        if (confirm('Are you sure you want to delete this user? This action cannot be undone.')) {
            this.userService.deleteUser(userId).subscribe({
                next: () => {
                    this.snackBar.open('User deleted successfully', 'Close', { duration: 3000 });
                    this.loadUsers(); // Reload the user list
                },
                error: (error) => {
                    const errorMsg = error.error?.message || 'Failed to delete user. Try again.';
                    this.snackBar.open(errorMsg, 'Close', { duration: 3000 });
                }
            });
        }
    }
    navigateToDashboard(): void {
        this.router.navigate(['/dashboard']);
    }
}
