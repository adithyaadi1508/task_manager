import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [CommonModule, MatButtonModule, MatIconModule],
  template: `
    <div class="not-found-container">
      <mat-icon>error_outline</mat-icon>
      <h1>404</h1>
      <h2>Page Not Found</h2>
      <p>The page you're looking for doesn't exist.</p>
      <button mat-raised-button color="primary" (click)="goHome()">
        <mat-icon>home</mat-icon> Go to Dashboard
      </button>
    </div>
  `,
  styles: [`
    .not-found-container {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      text-align: center;
      background-color: #f5f5f5;
    }

    mat-icon {
      font-size: 120px;
      width: 120px;
      height: 120px;
      color: #ff5252;
      margin-bottom: 20px;
    }

    h1 {
      font-size: 72px;
      margin: 0;
      color: #333;
    }

    h2 {
      font-size: 32px;
      margin: 10px 0;
      color: #666;
    }

    p {
      font-size: 18px;
      color: #999;
      margin-bottom: 30px;
    }

    button {
      padding: 12px 30px;
    }
  `]
})
export class NotFoundComponent {
  constructor(private router: Router) {}

  goHome(): void {
    this.router.navigate(['/dashboard']);
  }
}
