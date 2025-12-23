package com.project.task_manager.dto.response;

import com.project.task_manager.enums.Priority;
import com.project.task_manager.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Long id;

    private String title;

    private String description;

    private TaskStatus status;

    private Priority priority;

    private LocalDate startDate;

    private LocalDate dueDate;

    private Instant completedAt;

    private ProjectShortResponse project;

    private UserSummary assignedTo;

    private UserSummary createdBy;

    private List<String> tags;

    private Instant createdAt;

    private Instant updatedAt;
}
