import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { filter } from 'rxjs/operators';
import { MenuItem } from '../../models/user.model';

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

    menuItems: MenuItem[] = [  // CHANGE TYPE HERE
        { icon: 'dashboard', label: 'Dashboard', route: '/dashboard' },
        { icon: 'folder', label: 'Projects', route: '/projects' },
        { icon: 'assignment', label: 'My Tasks', route: '/tasks' },
        {
            icon: 'group',
            label: 'Manage Team',
            expanded: false,
            children: [
                { icon: 'person_add', label: 'Manage Users', route: '/team/users' },
                { icon: 'admin_panel_settings', label: 'User Roles', route: '/team/roles' },
                { icon: 'security', label: 'Permissions', route: '/team/permissions' }
            ]
        }
    ];

    constructor(private router: Router) {
        this.router.events.pipe(
            filter(event => event instanceof NavigationEnd)
        ).subscribe((event: any) => {
            this.currentRoute = event.url;
            this.autoExpandActiveMenu();
        });
    }

    toggleSidebar() {
        this.isExpanded = !this.isExpanded;
    }

    isActive(route?: string): boolean {
        if (!route) return false;
        return this.currentRoute.startsWith(route);
    }
    toggleSubmenu(item: MenuItem): void {
        if (item.children) {
            item.expanded = !item.expanded;
            this.isExpanded = true; // Keep sidebar expanded when clicking submenu
        }
    }

    hasActiveChild(item: MenuItem): boolean {
        if (!item.children) return false;
        return item.children.some(child => this.isActive(child.route));
    }

    private autoExpandActiveMenu(): void {
        this.menuItems.forEach(item => {
            if (item.children && this.hasActiveChild(item)) {
                item.expanded = true;
            }
        });
    }
}
