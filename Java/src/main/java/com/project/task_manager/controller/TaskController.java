package com.project.task_manager.controller;

import com.project.task_manager.dto.request.TaskRequest;
import com.project.task_manager.dto.response.MessageResponse;
import com.project.task_manager.dto.response.TaskResponse;
import com.project.task_manager.enums.Priority;
import com.project.task_manager.enums.TaskStatus;
import com.project.task_manager.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "bearerAuth")
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")  // Only ADMIN and MANAGER can create tasks
    @Operation(
            summary = "Create task",
            description = "Create a new task in a project. Only ADMIN and MANAGER can create tasks."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Task created successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN and MANAGER can create tasks")
    })
    public ResponseEntity<TaskResponse> createTask(
            @Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")  // All authenticated users
    @Operation(
            summary = "Get task by ID",
            description = "Retrieve detailed information for a specific task."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task found",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<TaskResponse> getTaskById(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id) {
        TaskResponse response = taskService.getTaskById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")  // All authenticated users
    @Operation(
            summary = "Get tasks for project",
            description = "Retrieve all tasks for a given project."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<TaskResponse>> getTasksForProject(
            @Parameter(description = "Project ID", required = true, example = "1")
            @PathVariable Long projectId) {
        List<TaskResponse> tasks = taskService.getTasksForProject(projectId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/my-tasks")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")  // All authenticated users
    @Operation(
            summary = "Get my tasks",
            description = "Retrieve all tasks assigned to the currently authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<TaskResponse>> getMyTasks() {
        List<TaskResponse> tasks = taskService.getTasksForCurrentUser();
        return ResponseEntity.ok(tasks);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")  // MEMBER can update their assigned tasks
    @Operation(
            summary = "Update task",
            description = "Update details of an existing task. ADMIN and MANAGER can update all fields. MEMBER can only update status of their assigned tasks."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task updated successfully",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Not authorized to update this task")
    })
    public ResponseEntity<TaskResponse> updateTask(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {
        TaskResponse response = taskService.updateTask(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")  // Only ADMIN and MANAGER can delete tasks
    @Operation(
            summary = "Delete task",
            description = "Delete a task by its ID. Only ADMIN and MANAGER can delete tasks."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Task deleted successfully",
                    content = @Content(schema = @Schema(implementation = MessageResponse.class))
            ),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN and MANAGER can delete tasks")
    })
    public ResponseEntity<MessageResponse> deleteTask(
            @Parameter(description = "Task ID", required = true, example = "1")
            @PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(new MessageResponse(true, "Task deleted successfully"));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")  // All authenticated users
    @Operation(
            summary = "Search tasks",
            description = "Search tasks by keyword in title or description."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<TaskResponse>> searchTasks(
            @Parameter(description = "Search keyword", example = "backend")
            @RequestParam(required = false) String keyword) {
        List<TaskResponse> tasks = taskService.searchTasks(keyword);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")  // All authenticated users
    @Operation(
            summary = "Filter tasks",
            description = "Filter tasks by project, status, priority, assignee, and keyword."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<TaskResponse>> filterTasks(
            @Parameter(description = "Project ID", example = "1")
            @RequestParam(required = false) Long projectId,
            @Parameter(description = "Task status")
            @RequestParam(required = false) TaskStatus status,
            @Parameter(description = "Task priority")
            @RequestParam(required = false) Priority priority,
            @Parameter(description = "Assignee user ID", example = "5")
            @RequestParam(required = false) Long assignedToId,
            @Parameter(description = "Search keyword", example = "UI")
            @RequestParam(required = false) String keyword) {

        List<TaskResponse> tasks = taskService.searchTasksWithFilters(
                projectId, status, priority, assignedToId, keyword
        );
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/filter/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")  // All authenticated users
    @Operation(
            summary = "Filter tasks by status",
            description = "Retrieve tasks matching a specific status."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<TaskResponse>> filterByStatus(
            @Parameter(description = "Task status", example = "IN_PROGRESS")
            @PathVariable TaskStatus status) {
        List<TaskResponse> tasks = taskService.filterTasksByStatus(status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/filter/priority/{priority}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")  // All authenticated users
    @Operation(
            summary = "Filter tasks by priority",
            description = "Retrieve tasks matching a specific priority."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<TaskResponse>> filterByPriority(
            @Parameter(description = "Task priority", example = "HIGH")
            @PathVariable Priority priority) {
        List<TaskResponse> tasks = taskService.filterTasksByPriority(priority);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")  // Only ADMIN and MANAGER can view all overdue tasks
    @Operation(
            summary = "Get overdue tasks",
            description = "Retrieve all overdue tasks in the system. Only ADMIN and MANAGER can access."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN and MANAGER")
    })
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        List<TaskResponse> tasks = taskService.getOverdueTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/my-overdue")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")  // All authenticated users
    @Operation(
            summary = "Get my overdue tasks",
            description = "Retrieve overdue tasks assigned to the current user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<TaskResponse>> getMyOverdueTasks() {
        List<TaskResponse> tasks = taskService.getMyOverdueTasks();
        return ResponseEntity.ok(tasks);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")  // Only ADMIN can view all tasks
    @Operation(
            summary = "Get all tasks (paginated) - ADMIN only",
            description = "Retrieve all tasks with pagination and sorting. Only ADMIN can access."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Only ADMIN")
    })
    public ResponseEntity<Page<TaskResponse>> getAllTasksPaginated(
            @Parameter(description = "Pagination and sorting parameters", hidden = true)
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<TaskResponse> tasks = taskService.getAllTasksPaginated(pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/paginated/project/{projectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")  // All authenticated users
    @Operation(
            summary = "Get tasks for project (paginated)",
            description = "Retrieve tasks for a specific project with pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<TaskResponse>> getTasksForProjectPaginated(
            @Parameter(description = "Project ID", required = true, example = "1")
            @PathVariable Long projectId,
            @Parameter(description = "Pagination and sorting parameters", hidden = true)
            @PageableDefault(size = 10, sort = "dueDate", direction = Sort.Direction.ASC)
            Pageable pageable) {
        Page<TaskResponse> tasks = taskService.getTasksForProjectPaginated(projectId, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/paginated/my-tasks")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")  // All authenticated users
    @Operation(
            summary = "Get my tasks (paginated)",
            description = "Retrieve tasks assigned to the current user with pagination."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<TaskResponse>> getMyTasksPaginated(
            @Parameter(description = "Pagination and sorting parameters", hidden = true)
            @PageableDefault(size = 10, sort = "dueDate", direction = Sort.Direction.ASC)
            Pageable pageable) {
        Page<TaskResponse> tasks = taskService.getMyTasksPaginated(pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/paginated/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")  // All authenticated users
    @Operation(
            summary = "Search tasks (paginated)",
            description = "Search tasks by keyword with pagination and sorting."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tasks retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<Page<TaskResponse>> searchTasksPaginated(
            @Parameter(description = "Search keyword", example = "bug")
            @RequestParam String keyword,
            @Parameter(description = "Pagination and sorting parameters", hidden = true)
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Page<TaskResponse> tasks = taskService.searchTasksPaginated(keyword, pageable);
        return ResponseEntity.ok(tasks);
    }
}
