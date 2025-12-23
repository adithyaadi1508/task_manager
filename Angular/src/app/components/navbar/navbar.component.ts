import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ThemeService } from '../../services/theme.service';
import { MatIconModule } from '@angular/material/icon';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    MatIconModule,
    MatToolbarModule,
    MatButtonModule,
    CommonModule
  ],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  @Input() title: string = 'Task Manager';
  @Input() showBackButton: boolean = false;
  @Input() showNewButton: boolean = false;
  @Input() newButtonText: string = 'New';
  @Input() newButtonIcon: string = 'add';
  @Input() username?: string;

  @Output() backClick = new EventEmitter<void>();
  @Output() newClick = new EventEmitter<void>();

  constructor(
    private router: Router,
    private authService: AuthService,
    protected themeService: ThemeService
  ) { }

  onBackClick(): void {
    this.backClick.emit();
  }

  onNewClick(): void {
    this.newClick.emit();
  }

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
