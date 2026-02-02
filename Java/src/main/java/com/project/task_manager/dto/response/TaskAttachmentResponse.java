package com.project.task_manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskAttachmentResponse {
    private Long id;
    private String fileName;
    private String storedFileName;
    private String fileType;
    private Long fileSize;
    private Instant uploadedAt;
    // Constructor, getters, setters
}
