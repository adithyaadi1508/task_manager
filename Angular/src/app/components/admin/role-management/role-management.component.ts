import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RoleService } from '../../../services/role.service';
import { Role } from '../../../models/user.model';
import { RoleDialogComponent } from '../role-dialog/role-dialog.component';
import { NavbarComponent } from '../../navbar/navbar.component';
import { Router } from '@angular/router';

@Component({
  selector: 'app-role-management',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatDialogModule,
    MatSnackBarModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    NavbarComponent
  ],
  templateUrl: './role-management.component.html',
  styleUrl: './role-management.component.css'
})
export class RoleManagementComponent implements OnInit {
  roles: Role[] = [];
  displayedColumns: string[] = ['name', 'description', 'actions'];
  isLoading = false;

  constructor(
    private roleService: RoleService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private router: Router,
  ) { }

  ngOnInit(): void {
    this.loadRoles();
  }

  loadRoles(): void {
    this.isLoading = true;
    this.roleService.getAllRoles().subscribe({
      next: (roles) => {
        this.roles = roles;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading roles:', error);
        this.showMessage('Failed to load roles', 'error');
        this.isLoading = false;
      }
    });
  }

  openAddDialog(): void {
    const dialogRef = this.dialog.open(RoleDialogComponent, {
      width: '500px',
      data: null,
      panelClass: 'role-dialog-container'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.createRole(result);
      }
    });
  }

  openEditDialog(role: Role): void {
    const dialogRef = this.dialog.open(RoleDialogComponent, {
      width: '500px',
      data: { ...role },
      panelClass: 'role-dialog-container'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && role.id) {
        this.updateRole(role.id, result);
      }
    });
  }

  createRole(roleData: { name: string; description: string }): void {
    this.roleService.createRole(roleData).subscribe({
      next: (newRole) => {
        this.roles.push(newRole);
        this.showMessage('Role created successfully', 'success');
        this.loadRoles(); // Reload to ensure consistency
      },
      error: (error) => {
        console.error('Error creating role:', error);
        this.showMessage('Failed to create role', 'error');
      }
    });
  }

  updateRole(id: number, roleData: { name: string; description: string }): void {
    this.roleService.updateRole(id, roleData).subscribe({
      next: (updatedRole) => {
        const index = this.roles.findIndex(r => r.id === id);
        if (index !== -1) {
          this.roles[index] = updatedRole;
        }
        this.showMessage('Role updated successfully', 'success');
        this.loadRoles(); // Reload to ensure consistency
      },
      error: (error) => {
        console.error('Error updating role:', error);
        this.showMessage('Failed to update role', 'error');
      }
    });
  }

  deleteRole(role: Role): void {
    if (!role.id) return;

    const confirmDelete = confirm(`Are you sure you want to delete the role "${role.name}"?`);
    if (!confirmDelete) return;

    this.roleService.deleteRole(role.id).subscribe({
      next: () => {
        this.roles = this.roles.filter(r => r.id !== role.id);
        this.showMessage('Role deleted successfully', 'success');
      },
      error: (error) => {
        console.error('Error deleting role:', error);
        this.showMessage('Failed to delete role', 'error');
      }
    });
  }

  private showMessage(message: string, type: 'success' | 'error'): void {
    this.snackBar.open(message, 'Close', {
      duration: 3000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: type === 'success' ? 'success-snackbar' : 'error-snackbar'
    });
  }

  navigateToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }
}
