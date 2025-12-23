package com.project.task_manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectStatsResponse {
    private Long projectId;
    private String projectName;

    // Task counts
    private Long totalTasks;
    private Long completedTasks;
    private Long inProgressTasks;
    private Long todoTasks;

    // Progress
    private Double completionRate;
    private Long overdueTasksCount;

    // Breakdown maps
    private Map<String, Long> tasksByPriority;
    private Map<String, Long> tasksByStatus;
}
