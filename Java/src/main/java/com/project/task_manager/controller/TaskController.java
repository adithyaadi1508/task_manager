package com.project.task_manager.controller;

import com.project.task_manager.config.swagger.*;
import com.project.task_manager.dto.request.TaskRequest;
import com.project.task_manager.dto.response.MessageResponse;
import com.project.task_manager.dto.response.TaskResponse;
import com.project.task_manager.enums.Priority;
import com.project.task_manager.enums.TaskStatus;
import com.project.task_manager.service.TaskService;
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

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "APIs for managing tasks, assignments, and task tracking")
@StandardApiResponses
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @PostApiResponses  // POST = Create → 201, 400, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create task")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @GetApiResponses  // GET by ID → 200, 404, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/project/{projectId}")
    @GetListApiResponses  // GET list → 200, 401 (no 404)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Get tasks for project")
    public ResponseEntity<List<TaskResponse>> getTasksForProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.getTasksForProject(projectId));
    }

    @GetMapping("/my-tasks")
    @GetListApiResponses  // GET list → 200, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Get my tasks")
    public ResponseEntity<List<TaskResponse>> getMyTasks() {
        return ResponseEntity.ok(taskService.getTasksForCurrentUser());
    }

    @PutMapping("/{id}")
    @PutApiResponses  // PUT = Update → 200, 400, 404, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Update task")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        return ResponseEntity.ok(taskService.updateTask(id, request));
    }

    @DeleteMapping("/{id}")
    @DeleteApiResponses  // DELETE → 200, 404, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete task")
    public ResponseEntity<MessageResponse> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(new MessageResponse(true, "Task deleted successfully"));
    }

    @GetMapping("/search")
    @GetListApiResponses  // Search returns list → 200, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Search tasks")
    public ResponseEntity<List<TaskResponse>> searchTasks(
            @Parameter(description = "Search keyword", example = "backend")
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(taskService.searchTasks(keyword));
    }

    @GetMapping("/filter")
    @GetListApiResponses  // Filter returns list → 200, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Filter tasks")
    public ResponseEntity<List<TaskResponse>> filterTasks(
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) TaskStatus status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long assignedToId,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(taskService.searchTasksWithFilters(
                projectId, status, priority, assignedToId, keyword));
    }

    @GetMapping("/filter/status/{status}")
    @GetListApiResponses  // Filter returns list → 200, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Filter tasks by status")
    public ResponseEntity<List<TaskResponse>> filterByStatus(@PathVariable TaskStatus status) {
        return ResponseEntity.ok(taskService.filterTasksByStatus(status));
    }

    @GetMapping("/filter/priority/{priority}")
    @GetListApiResponses  // Filter returns list → 200, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Filter tasks by priority")
    public ResponseEntity<List<TaskResponse>> filterByPriority(@PathVariable Priority priority) {
        return ResponseEntity.ok(taskService.filterTasksByPriority(priority));
    }

    @GetMapping("/overdue")
    @GetListApiResponses  // Returns list → 200, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get overdue tasks")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        return ResponseEntity.ok(taskService.getOverdueTasks());
    }

    @GetMapping("/my-overdue")
    @GetListApiResponses  // Returns list → 200, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Get my overdue tasks")
    public ResponseEntity<List<TaskResponse>> getMyOverdueTasks() {
        return ResponseEntity.ok(taskService.getMyOverdueTasks());
    }

    @GetMapping
    @GetListApiResponses  // Paginated list → 200, 401
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all tasks (paginated) - ADMIN only")
    public ResponseEntity<Page<TaskResponse>> getAllTasksPaginated(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllTasksPaginated(pageable));
    }

    @GetMapping("/paginated/project/{projectId}")
    @GetListApiResponses  // Paginated list → 200, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Get tasks for project (paginated)")
    public ResponseEntity<Page<TaskResponse>> getTasksForProjectPaginated(
            @PathVariable Long projectId,
            @PageableDefault(size = 10, sort = "dueDate", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getTasksForProjectPaginated(projectId, pageable));
    }

    @GetMapping("/paginated/my-tasks")
    @GetListApiResponses  // Paginated list → 200, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Get my tasks (paginated)")
    public ResponseEntity<Page<TaskResponse>> getMyTasksPaginated(
            @PageableDefault(size = 10, sort = "dueDate", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ResponseEntity.ok(taskService.getMyTasksPaginated(pageable));
    }

    @GetMapping("/paginated/search")
    @GetListApiResponses  // Search paginated → 200, 401
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Search tasks (paginated)")
    public ResponseEntity<Page<TaskResponse>> searchTasksPaginated(
            @RequestParam String keyword,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        return ResponseEntity.ok(taskService.searchTasksPaginated(keyword, pageable));
    }
}
