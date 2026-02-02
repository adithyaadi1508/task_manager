// src/app/services/task-attachment.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface TaskAttachment {
  id: number;
  fileName: string;
  storedFileName?: string;
  filePath?: string;
  fileType: string;
  fileSize: number;
  uploadedAt: string;
  uploadedBy?: {
    id: number;
    username: string;
    fullName: string;
  };
}

export interface AttachmentUploadResponse {
  id: number;
  fileName: string;
  fileType: string;
  fileSize: number;
  uploadedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class TaskAttachmentService {
  private apiUrl = `${environment.apiUrl}/tasks`;

  constructor(private http: HttpClient) {}

  /**
   * Upload a file attachment for a task
   * @param taskId The task ID
   * @param file The file to upload
   * @returns Observable of the upload response
   */
  uploadAttachment(taskId: number, file: File): Observable<AttachmentUploadResponse> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post<AttachmentUploadResponse>(
      `${this.apiUrl}/${taskId}/attachments`,
      formData
    );
  }

  /**
   * Get all attachments for a specific task
   * @param taskId The task ID
   * @returns Observable array of task attachments
   */
  getTaskAttachments(taskId: number): Observable<TaskAttachment[]> {
    return this.http.get<TaskAttachment[]>(`${this.apiUrl}/${taskId}/attachments`);
  }

  /**
   * Download an attachment file
   * @param attachmentId The attachment ID
   * @returns Observable Blob of the file
   */
  downloadAttachment(attachmentId: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/attachments/${attachmentId}`, {
      responseType: 'blob'
    });
  }

  /**
   * Delete an attachment
   * @param attachmentId The attachment ID to delete
   * @returns Observable void
   */
  deleteAttachment(attachmentId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/attachments/${attachmentId}`);
  }

  /**
   * Helper method to get file icon based on file extension
   * @param fileName The file name
   * @returns Material icon name
   */
  getFileIcon(fileName: string): string {
    const extension = fileName.split('.').pop()?.toLowerCase();
    switch (extension) {
      case 'pdf': return 'picture_as_pdf';
      case 'doc':
      case 'docx': return 'description';
      case 'xlsx':
      case 'xls': return 'table_chart';
      case 'jpg':
      case 'jpeg':
      case 'png':
      case 'gif': return 'image';
      default: return 'attach_file';
    }
  }

  /**
   * Helper method to format file size
   * @param bytes File size in bytes
   * @returns Formatted file size string
   */
  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 B';
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(2) + ' MB';
  }
}
