package com.project.task_manager.service.impl;

import com.project.task_manager.dto.response.TaskAttachmentResponse;
import com.project.task_manager.exception.BadRequestException;
import com.project.task_manager.exception.FileStorageException;
import com.project.task_manager.exception.ResourceNotFoundException;
import com.project.task_manager.model.Task;
import com.project.task_manager.model.TaskAttachment;
import com.project.task_manager.model.User;
import com.project.task_manager.repository.TaskAttachmentRepository;
import com.project.task_manager.repository.TaskRepository;
import com.project.task_manager.service.TaskAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskAttachmentServiceImpl implements TaskAttachmentService {

    private final TaskAttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public TaskAttachment storeFile(Long taskId, MultipartFile file, User uploadedBy) throws IOException {
        if (file.isEmpty()) {
            throw new BadRequestException("Cannot store empty file");
        }

        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());

        if (originalFileName.contains("..")) {
            throw new BadRequestException("Invalid file path sequence: " + originalFileName);
        }

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        // Create unique filename
        String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        // Create directory structure: uploads/tasks/{taskId}/
        Path uploadPath = Paths.get(uploadDir, "tasks", String.valueOf(taskId));

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(storedFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            TaskAttachment attachment = new TaskAttachment();
            attachment.setFileName(originalFileName);
            attachment.setStoredFileName(storedFileName);
            attachment.setFilePath(filePath.toString());
            attachment.setFileType(file.getContentType());
            attachment.setFileSize(file.getSize());
            attachment.setTask(task);
            attachment.setUploadedBy(uploadedBy);

            return attachmentRepository.save(attachment);
        } catch (IOException ex) {
            throw new FileStorageException("Failed to store file: " + originalFileName, ex);
        }
    }

    @Override
    public TaskAttachment getAttachment(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + id));
    }

    @Override
    public List<TaskAttachmentResponse> getTaskAttachments(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new ResourceNotFoundException("Task not found with id: " + taskId);
        }

        List<TaskAttachment> attachments = attachmentRepository.findByTaskId(taskId);

        return attachments.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteAttachment(Long id) {
        TaskAttachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + id));

        // Delete physical file
        try {
            Path filePath = Paths.get(attachment.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Failed to delete file: " + attachment.getFileName(), ex);
        }

        // Delete database record
        attachmentRepository.deleteById(id);
    }

    public Resource loadFileAsResource(Long attachmentId) {
        TaskAttachment attachment = getAttachment(attachmentId);

        try {
            Path filePath = Paths.get(attachment.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException("File not found: " + attachment.getFileName());
            }
        } catch (MalformedURLException ex) {
            throw new FileStorageException("File not found: " + attachment.getFileName(), ex);
        }
    }
    private TaskAttachmentResponse mapToResponse(TaskAttachment attachment) {
        TaskAttachmentResponse response = new TaskAttachmentResponse();
        response.setId(attachment.getId());
        response.setFileName(attachment.getFileName());
        response.setStoredFileName(attachment.getStoredFileName());
        response.setFileType(attachment.getFileType());
        response.setFileSize(attachment.getFileSize());
        response.setUploadedAt(attachment.getUploadedAt());
        return response;
    }
}
