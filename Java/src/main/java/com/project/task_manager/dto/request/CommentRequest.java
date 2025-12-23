package com.project.task_manager.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {

    @NotNull(message = "Task ID is required")
    private Long taskId;

    private Long parentCommentId;  // For threaded replies (optional)

    @NotBlank(message = "Comment content is required")
    private String content;
}
