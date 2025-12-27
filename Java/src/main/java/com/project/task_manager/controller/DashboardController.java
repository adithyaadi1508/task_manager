package com.project.task_manager.controller;

import com.project.task_manager.config.swagger.*;
import com.project.task_manager.dto.response.DashboardStatsResponse;
import com.project.task_manager.dto.response.ProjectStatsResponse;
import com.project.task_manager.dto.response.UserWorkloadResponse;
import com.project.task_manager.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard & Analytics", description = "APIs for viewing statistics, analytics, and workload information")
@StandardApiResponses
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @StandardApiResponses  // Returns single stats object → 200, 401
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get overall system statistics (ADMIN only)")
    public ResponseEntity<DashboardStatsResponse> getOverallStats() {
        return ResponseEntity.ok(dashboardService.getOverallStats());
    }

    @GetMapping("/my-stats")
    @StandardApiResponses  // Returns single stats object → 200, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Get personal dashboard statistics")
    public ResponseEntity<DashboardStatsResponse> getMyDashboardStats() {
        return ResponseEntity.ok(dashboardService.getMyDashboardStats());
    }

    @GetMapping("/projects/{projectId}/stats")
    @GetApiResponses  // GET by ID → 200, 404, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Get project statistics")
    public ResponseEntity<ProjectStatsResponse> getProjectStats(@PathVariable Long projectId) {
        return ResponseEntity.ok(dashboardService.getProjectStats(projectId));
    }

    @GetMapping("/my-workload")
    @StandardApiResponses  // Returns single workload object → 200, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Get personal workload information")
    public ResponseEntity<UserWorkloadResponse> getMyWorkload() {
        return ResponseEntity.ok(dashboardService.getMyWorkload());
    }

    @GetMapping("/users/{userId}/workload")
    @GetApiResponses  // GET by ID → 200, 404, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get user workload information (ADMIN/MANAGER only)")
    public ResponseEntity<UserWorkloadResponse> getUserWorkload(@PathVariable Long userId) {
        return ResponseEntity.ok(dashboardService.getUserWorkload(userId));
    }
}
