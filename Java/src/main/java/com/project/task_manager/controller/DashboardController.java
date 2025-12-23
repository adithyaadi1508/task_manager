package com.project.task_manager.controller;

import com.project.task_manager.dto.response.DashboardStatsResponse;
import com.project.task_manager.dto.response.MessageResponse;
import com.project.task_manager.dto.response.ProjectStatsResponse;
import com.project.task_manager.dto.response.UserWorkloadResponse;
import com.project.task_manager.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard & Analytics", description = "APIs for viewing statistics, analytics, and workload information")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")  // Only ADMIN can view system-wide stats
    @Operation(
            summary = "Get overall system statistics (ADMIN only)",
            description = "Retrieve system-wide statistics including total projects, tasks, users, and completion rates. Only accessible by ADMIN users."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DashboardStatsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Only ADMIN users can access overall statistics",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))
            )
    })
    public ResponseEntity<DashboardStatsResponse> getOverallStats() {
        DashboardStatsResponse stats = dashboardService.getOverallStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/my-stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")  // All authenticated users
    @Operation(
            summary = "Get personal dashboard statistics",
            description = "Retrieve dashboard statistics for the currently logged-in user, including their projects, assigned tasks, completion rate, and pending items."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Personal statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DashboardStatsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))
            )
    })
    public ResponseEntity<DashboardStatsResponse> getMyDashboardStats() {
        DashboardStatsResponse stats = dashboardService.getMyDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/projects/{projectId}/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")  // All authenticated users (service checks project access)
    @Operation(
            summary = "Get project statistics",
            description = "Retrieve detailed statistics for a specific project including task distribution by status, priority breakdown, team members, and completion progress. Only accessible to project members."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Project statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProjectStatsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - User not a member of this project",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Project not found",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))
            )
    })
    public ResponseEntity<ProjectStatsResponse> getProjectStats(
            @Parameter(description = "ID of the project to retrieve statistics for", required = true, example = "1")
            @PathVariable Long projectId) {
        ProjectStatsResponse stats = dashboardService.getProjectStats(projectId);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/my-workload")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")  // All authenticated users
    @Operation(
            summary = "Get personal workload information",
            description = "Retrieve workload distribution for the currently logged-in user including active tasks, overdue items, upcoming deadlines, and task breakdown by priority and status."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Personal workload retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserWorkloadResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))
            )
    })
    public ResponseEntity<UserWorkloadResponse> getMyWorkload() {
        UserWorkloadResponse workload = dashboardService.getMyWorkload();
        return ResponseEntity.ok(workload);
    }

    @GetMapping("/users/{userId}/workload")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")  // Only ADMIN and MANAGER can view other users' workload
    @Operation(
            summary = "Get user workload information (ADMIN/MANAGER only)",
            description = "Retrieve workload distribution for a specific user. Only ADMIN and MANAGER users can view other users' workload."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User workload retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserWorkloadResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid or missing JWT token",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Only ADMIN and MANAGER can view other users' workload",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))
            )
    })
    public ResponseEntity<UserWorkloadResponse> getUserWorkload(
            @Parameter(description = "ID of the user to retrieve workload for", required = true, example = "2")
            @PathVariable Long userId) {
        UserWorkloadResponse workload = dashboardService.getUserWorkload(userId);
        return ResponseEntity.ok(workload);
    }
}
