import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { filter } from 'rxjs/operators';

@Component({
    selector: 'app-sidebar',
    standalone: true,
    imports: [CommonModule, RouterModule, MatIconModule, MatListModule],
    templateUrl: './sidebar.component.html',
    styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
    isExpanded = false;
    currentRoute = '';

    menuItems = [
        { icon: 'dashboard', label: 'Dashboard', route: '/dashboard' },
        { icon: 'folder', label: 'Projects', route: '/projects' },
        { icon: 'assignment', label: 'My Tasks', route: '/tasks' },
        { icon: 'group', label: 'Manage Team', route: '/team-management' } // Placeholder route
    ];

    constructor(private router: Router) {
        this.router.events.pipe(
            filter(event => event instanceof NavigationEnd)
        ).subscribe((event: any) => {
            this.currentRoute = event.url;
        });
    }

    toggleSidebar() {
        this.isExpanded = !this.isExpanded;
    }

    isActive(route: string): boolean {
        return this.currentRoute.startsWith(route);
    }
}
