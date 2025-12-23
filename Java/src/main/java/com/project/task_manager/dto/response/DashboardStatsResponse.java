package com.project.task_manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    // Overall counts
    private Long totalProjects;
    private Long totalTasks;
    private Long activeProjects;

    // Task breakdown by status
    private Long todoTasks;
    private Long inProgressTasks;
    private Long completedTasks;

    // Completion metrics
    private Double completionRate;
    private Long overdueTasksCount;  // Match your service

    // Breakdown maps
    private Map<String, Long> tasksByPriority;
    private Map<String, Long> tasksByStatus;
}
