package com.project.task_manager.dto.response;

import com.project.task_manager.enums.ReferenceType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long id;

    private String type;          // e.g. "TASK_ASSIGNED", "COMMENT_ADDED"

    private String title;

    private String message;

    private ReferenceType referenceType;  // TASK, PROJECT, COMMENT, USER

    private Long referenceId;     // ID of the task/project/comment

    private Boolean isRead;

    private Instant createdAt;

    private Instant readAt;

    private UserSummary user;     // who receives the notification
}
