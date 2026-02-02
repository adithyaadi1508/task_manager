package com.project.task_manager.controller;

import com.project.task_manager.dto.response.TaskAttachmentResponse;
import com.project.task_manager.model.TaskAttachment;
import com.project.task_manager.model.User;
import com.project.task_manager.service.TaskAttachmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskAttachmentController {

    private final TaskAttachmentService attachmentService;

    @PostMapping("/{taskId}/attachments")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser) {
        try {
            TaskAttachment attachment = attachmentService.storeFile(taskId, file, currentUser);

            Map<String, Object> response = new HashMap<>();
            response.put("id", attachment.getId());
            response.put("fileName", attachment.getFileName());
            response.put("fileType", attachment.getFileType());
            response.put("fileSize", attachment.getFileSize());
            response.put("uploadedAt", attachment.getUploadedAt());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{taskId}/attachments")
    public ResponseEntity<List<TaskAttachmentResponse>> getTaskAttachments(@PathVariable Long taskId) {
        List<TaskAttachmentResponse> attachments = attachmentService.getTaskAttachments(taskId);
        return ResponseEntity.ok(attachments);
    }

    @GetMapping("/attachments/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        // Load file as Resource (not from database blob)
        Resource resource = attachmentService.loadFileAsResource(id);
        TaskAttachment attachment = attachmentService.getAttachment(id);

        // Determine content type
        String contentType = attachment.getFileType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/attachments/{id}")
    public ResponseEntity<Void> deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
        return ResponseEntity.noContent().build();
    }
}
