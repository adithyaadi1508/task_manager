package com.project.task_manager.controller;

import com.project.task_manager.config.swagger.*;
import com.project.task_manager.dto.request.ProjectRequest;
import com.project.task_manager.dto.response.MessageResponse;
import com.project.task_manager.dto.response.ProjectResponse;
import com.project.task_manager.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Tag(name = "Project Management", description = "APIs for managing projects and project lifecycles")
@StandardApiResponses
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @PostApiResponses  // POST = Create → 201, 400, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create a new project")
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse response = projectService.createProject(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @GetApiResponses  // GET by ID → 200, 404, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Get project by ID")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @GetMapping("/my-projects")
    @GetListApiResponses  // GET list → 200, 401
    @Operation(summary = "Get current user's projects")
    public ResponseEntity<List<ProjectResponse>> getMyProjects() {
        return ResponseEntity.ok(projectService.getProjectsForCurrentUser());
    }

    @PutMapping("/{id}")
    @PutApiResponses  // PUT = Update → 200, 400, 404, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update project")
    public ResponseEntity<ProjectResponse> updateProject(
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request) {
        return ResponseEntity.ok(projectService.updateProject(id, request));
    }

    @DeleteMapping("/{id}")
    @DeleteApiResponses  // DELETE → 200, 404, 401
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete project")
    public ResponseEntity<MessageResponse> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok(new MessageResponse(true, "Project deleted successfully"));
    }

    @GetMapping
    @GetListApiResponses  // Paginated list → 200, 401
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all projects (paginated)")
    public ResponseEntity<Page<ProjectResponse>> getAllProjectsPaginated(
            @PageableDefault(size = 5, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(projectService.getAllProjectsPaginated(pageable));
    }

    @GetMapping("/paginated/my-projects")
    @GetListApiResponses  // Paginated list → 200, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Get current user's projects (paginated)")
    public ResponseEntity<Page<ProjectResponse>> getMyProjectsPaginated(
            @PageableDefault(size = 5, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ResponseEntity.ok(projectService.getMyProjectsPaginated(pageable));
    }

    // Team Management APIs

    @PostMapping("/{projectId}/team")
    @PostApiResponses  // POST → 201, 400, 401
    @Operation(summary = "Add team member to project")
    public ResponseEntity<?> addTeamMember(
            @Parameter(description = "Project ID", example = "1")
            @PathVariable Long projectId,
            @RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String role = request.get("role").toString();
            projectService.addTeamMember(projectId, userId, role);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{projectId}/team/{userId}")
    @DeleteApiResponses  // DELETE → 200, 404, 401
    @Operation(summary = "Remove team member from project")
    public ResponseEntity<?> removeTeamMember(
            @Parameter(description = "Project ID", example = "1")
            @PathVariable Long projectId,
            @Parameter(description = "User ID to remove", example = "5")
            @PathVariable Long userId) {
        try {
            projectService.removeTeamMember(projectId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/{projectId}/team")
    @GetListApiResponses  // GET list → 200, 401
    @Operation(summary = "Get project team members")
    public ResponseEntity<List<?>> getProjectTeam(
            @Parameter(description = "Project ID", example = "1")
            @PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectTeam(projectId));
    }
}
