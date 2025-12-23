package com.project.task_manager.service;

import com.project.task_manager.dto.response.DashboardStatsResponse;
import com.project.task_manager.dto.response.ProjectStatsResponse;
import com.project.task_manager.dto.response.UserWorkloadResponse;

public interface DashboardService {

    DashboardStatsResponse getOverallStats();

    DashboardStatsResponse getMyDashboardStats();

    ProjectStatsResponse getProjectStats(Long projectId);

    UserWorkloadResponse getMyWorkload();

    UserWorkloadResponse getUserWorkload(Long userId);
}
