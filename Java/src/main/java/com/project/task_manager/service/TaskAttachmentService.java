package com.project.task_manager.service;

import com.project.task_manager.dto.response.TaskAttachmentResponse;
import com.project.task_manager.model.TaskAttachment;
import com.project.task_manager.model.User;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface TaskAttachmentService {

    /**
     * Store a file attachment for a task
     * @param taskId The ID of the task
     * @param file The file to upload
     * @param uploadedBy The user uploading the file
     * @return The saved TaskAttachment entity
     * @throws IOException if file storage fails
     */
    TaskAttachment storeFile(Long taskId, MultipartFile file, User uploadedBy) throws IOException;

    /**
     * Get attachment metadata by ID
     * @param id The attachment ID
     * @return The TaskAttachment entity
     */
    TaskAttachment getAttachment(Long id);

    /**
     * Get all attachments for a specific task
     * @param taskId The task ID
     * @return List of attachments
     */
    List<TaskAttachmentResponse> getTaskAttachments(Long taskId);

    /**
     * Delete an attachment (removes both file and database record)
     * @param id The attachment ID to delete
     */
    void deleteAttachment(Long id);

    /**
     * Load file as a Resource for downloading
     * @param attachmentId The attachment ID
     * @return Resource containing the file
     */
    Resource loadFileAsResource(Long attachmentId);
}
