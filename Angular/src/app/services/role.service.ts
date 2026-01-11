import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Role } from '../models/user.model';

@Injectable({
    providedIn: 'root'
})
export class RoleService {
    private apiUrl = `${environment.apiUrl}/users/roles`;

    constructor(private http: HttpClient) { }

    getAllRoles(): Observable<Role[]> {
        return this.http.get<Role[]>(this.apiUrl);
    }

    createRole(role: { name: string; description: string }): Observable<Role> {
        return this.http.post<Role>(this.apiUrl, role);
    }

    updateRole(id: number, role: { name: string; description: string }): Observable<Role> {

        return this.http.put<Role>(`${this.apiUrl}/${id}`, role);
    }

    deleteRole(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
