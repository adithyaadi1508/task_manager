package com.project.task_manager.dto.request;

import com.project.task_manager.enums.Priority;
import com.project.task_manager.enums.ProjectStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    private Long id;

    @Size(max = 100, message = "Project name must not exceed 100 characters")
    private String name;  // ← Removed @NotBlank

    private String description;

    private LocalDate startDate;  // ← Removed @NotNull

    private LocalDate endDate;

    private ProjectStatus status;

    private Priority priority;

    private List<Long> memberIds;
}
