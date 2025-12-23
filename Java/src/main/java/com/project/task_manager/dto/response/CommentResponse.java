package com.project.task_manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {

    private Long id;

    private Long taskId;

    private Long parentCommentId;

    private String content;

    private Boolean isEdited;

    private UserSummary user;

    private Instant createdAt;

    private Instant updatedAt;

    private List<CommentResponse> replies;
}
