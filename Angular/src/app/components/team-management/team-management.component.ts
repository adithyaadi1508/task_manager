import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';
import { ProjectService } from '../../services/project.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-team-management',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatListModule,
    MatFormFieldModule,
    MatSelectModule,
    MatSnackBarModule
  ],
  templateUrl: './team-management.component.html',
  styleUrl: './team-management.component.css'
})
export class TeamManagementComponent implements OnInit {
  teamMembers: any[] = [];
  availableUsers: any[] = [];
  selectedUserId: number | null = null;
  selectedRole: string = 'MEMBER';
  
roles = ['VIEWER', 'MEMBER', 'LEAD', 'MANAGER', 'ADMIN'];

  constructor(
    private projectService: ProjectService,
    private userService: UserService,
    private snackBar: MatSnackBar,
    private dialogRef: MatDialogRef<TeamManagementComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { projectId: number }
  ) {}

  ngOnInit(): void {
    this.loadTeamMembers();
    this.loadAvailableUsers();
  }

  loadTeamMembers(): void {
    this.projectService.getProjectTeam(this.data.projectId).subscribe({
      next: (members) => {
        this.teamMembers = members;
      },
      error: (error) => {
        console.error('Error loading team', error);
      }
    });
  }

  loadAvailableUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (users) => {
        // Filter out users already in the team
        this.availableUsers = users.filter(user => 
          !this.teamMembers.some(member => member.id === user.id)
        );
      },
      error: (error) => {
        console.error('Error loading users', error);
      }
    });
  }

  addMember(): void {
    if (!this.selectedUserId) {
      this.snackBar.open('Please select a user', 'Close', { duration: 3000 });
      return;
    }

    this.projectService.addTeamMember(this.data.projectId, this.selectedUserId, this.selectedRole).subscribe({
      next: () => {
        this.snackBar.open('Team member added successfully', 'Close', { duration: 3000 });
        this.selectedUserId = null;
        this.selectedRole = 'MEMBER';
        this.loadTeamMembers();
        this.loadAvailableUsers();
      },
      error: (error) => {
        this.snackBar.open('Error adding team member', 'Close', { duration: 3000 });
      }
    });
  }

  removeMember(userId: number): void {
    if (confirm('Are you sure you want to remove this team member?')) {
      this.projectService.removeTeamMember(this.data.projectId, userId).subscribe({
        next: () => {
          this.snackBar.open('Team member removed', 'Close', { duration: 3000 });
          this.loadTeamMembers();
          this.loadAvailableUsers();
        },
        error: (error) => {
          this.snackBar.open('Error removing team member', 'Close', { duration: 3000 });
        }
      });
    }
  }

  close(): void {
    this.dialogRef.close(true);
  }
}
