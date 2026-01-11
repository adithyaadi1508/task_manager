export interface Role {
  id?: number;
  name: string;
  description: string;
}

export interface User {
  id?: number;
  username: string;
  email: string;
  password?: string;
  firstName: string;
  lastName: string;
  phone?: string;
  role?: string;
}

export interface LoginRequest {
  usernameOrEmail: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phone?: string;
  roles: string[];
}

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  phone?: string;
  roles?: string[];
}

export interface AuthResponse {
  accessToken: string;      // ← Changed from 'token'
  tokenType: string;         // ← Added
  user: UserResponse;        // ← Changed from 'username'
}

export interface UserSimple {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  roles?: string[];
}

export interface MenuItem {
  icon: string;              // Required: Icon name (e.g., 'dashboard', 'folder')
  label: string;             // Required: Display text (e.g., 'Dashboard', 'Projects')
  route?: string;            // Optional: Router path (e.g., '/dashboard')
  children?: MenuItem[];     // Optional: Array of child menu items (for nesting)
  expanded?: boolean;        // Optional: Is submenu expanded? (true/false)
}

