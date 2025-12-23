import { Injectable, signal } from '@angular/core';

export type Theme = 'light' | 'dark';

@Injectable({
    providedIn: 'root'
})
export class ThemeService {
    currentTheme = signal<Theme>('dark'); // Default to dark as per existing design

    constructor() {
        this.initTheme();
    }

    private initTheme(): void {
        const savedTheme = localStorage.getItem('theme') as Theme;
        if (savedTheme) {
            this.setTheme(savedTheme);
        } else {
            // Fallback to system preference if needed, or default to dark
            // For now, defaulting to dark to match current look
            this.setTheme('dark');
        }
    }

    setTheme(theme: Theme): void {
        this.currentTheme.set(theme);
        localStorage.setItem('theme', theme);
        document.documentElement.setAttribute('data-theme', theme);
    }

    toggleTheme(): void {
        const newTheme = this.currentTheme() === 'dark' ? 'light' : 'dark';
        this.setTheme(newTheme);
    }
}
