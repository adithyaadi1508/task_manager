package com.project.task_manager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWorkloadResponse {
    private Long userId;
    private String username;

    // Task counts
    private Long totalAssignedTasks;
    private Long completedTasks;
    private Long inProgressTasks;
    private Long todoTasks;
    private Long overdueTasksCount;

    // Metrics
    private Double completionRate;
    private Integer tasksCompletedThisWeek;
    private Integer tasksCompletedThisMonth;
}
