import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Project, ProjectStats } from '../models/project.model';

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = `${environment.apiUrl}/projects`;

  constructor(private http: HttpClient) {}

  getAllProjects(): Observable<Project[]> {
    return this.http.get<Project[]>(`${this.apiUrl}/my-projects`);
  }

  getProjectById(id: number): Observable<Project> {
    return this.http.get<Project>(`${this.apiUrl}/${id}`);
  }

  createProject(project: Project): Observable<Project> {
    return this.http.post<Project>(this.apiUrl, project);
  }

  updateProject(id: number, project: Project): Observable<Project> {
    return this.http.put<Project>(`${this.apiUrl}/${id}`, project);
  }

  deleteProject(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getProjectStats(id: number): Observable<ProjectStats> {
    return this.http.get<ProjectStats>(`${this.apiUrl}/${id}/stats`);
  }

  getProjectTeam(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${id}/team`);
  }

  addTeamMember(projectId: number, userId: number, role: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/${projectId}/team`, { userId, role });
  }

  removeTeamMember(projectId: number, userId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${projectId}/team/${userId}`);
  }

  getProjectTasks(projectId: number): Observable<any[]> {
    return this.http.get<any[]>(`${environment.apiUrl}/tasks/project/${projectId}`);
  }
}
