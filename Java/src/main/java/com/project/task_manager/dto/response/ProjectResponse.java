package com.project.task_manager.dto.response;

import com.project.task_manager.enums.Priority;
import com.project.task_manager.enums.ProjectStatus;
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
public class ProjectResponse {
    private Long id;

    private String name;

    private String description;

    private ProjectStatus status;

    private Priority priority;

    private LocalDate startDate;

    private LocalDate endDate;

    private UserSummary owner;

    private List<UserSummary> members;

    private Instant createdAt;

    private Instant updatedAt;
}
