import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiUrl}/auth`;
  private tokenKey = 'auth_token';
  private currentUserSubject = new BehaviorSubject<string | null>(this.getUsername());

  constructor(private http: HttpClient) {}

  register(user: RegisterRequest): Observable<any> {  
    return this.http.post(`${this.apiUrl}/register`, user);
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          console.log('Full Response:', JSON.stringify(response));
          console.log('Access Token:', response.accessToken);
          console.log('User:', response.user);
          localStorage.setItem(this.tokenKey, response.accessToken);        
          localStorage.setItem('username', response.user.username);          
          this.currentUserSubject.next(response.user.username); 
          console.log('Token saved:', localStorage.getItem(this.tokenKey));
        })
      );
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem('username');
    this.currentUserSubject.next(null);
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getUsername(): string | null {
    return localStorage.getItem('username');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): Observable<string | null> {
    return this.currentUserSubject.asObservable();
  }
}
