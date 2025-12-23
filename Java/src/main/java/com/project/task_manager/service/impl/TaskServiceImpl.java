package com.project.task_manager.service.impl;

import com.project.task_manager.dto.request.TaskRequest;
import com.project.task_manager.dto.response.ProjectShortResponse;
import com.project.task_manager.dto.response.TaskResponse;
import com.project.task_manager.dto.response.UserSummary;
import com.project.task_manager.enums.Priority;
import com.project.task_manager.enums.TaskStatus;
import com.project.task_manager.exception.ResourceNotFoundException;
import com.project.task_manager.model.Project;
import com.project.task_manager.model.Task;
import com.project.task_manager.model.User;
import com.project.task_manager.repository.ProjectRepository;
import com.project.task_manager.repository.TaskRepository;
import com.project.task_manager.repository.UserRepository;
import com.project.task_manager.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        User currentUser = getCurrentUser();

        // Validate project exists
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        // Create task
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setProject(project);
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO);
        task.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
        task.setStartDate(request.getStartDate());
        task.setDueDate(request.getDueDate());
        task.setCreatedBy(currentUser);

        // Assign to user if provided
        if (request.getAssignedToId() != null) {
            User assignee = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignedTo(assignee);
        }

        Task savedTask = taskRepository.save(task);

        return mapToTaskResponse(savedTask);
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        return mapToTaskResponse(task);
    }

    @Override
    public List<TaskResponse> getTasksForProject(Long projectId) {
        List<Task> tasks = taskRepository.findByProjectId(projectId);

        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getTasksForCurrentUser() {
        User currentUser = getCurrentUser();

        List<Task> tasks = taskRepository.findByAssignedToId(currentUser.getId());

        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        // Update fields
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
            // If completed, set completed timestamp
            if (request.getStatus() == TaskStatus.COMPLETED) {
                task.setCompletedAt(Instant.now());
            }
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getStartDate() != null) {
            task.setStartDate(request.getStartDate());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getAssignedToId() != null) {
            User assignee = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            task.setAssignedTo(assignee);
        }

        Task updatedTask = taskRepository.save(task);

        return mapToTaskResponse(updatedTask);
    }

    @Override
    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        taskRepository.delete(task);
    }
    // Add these methods to your existing TaskServiceImpl class

    @Override
    public List<TaskResponse> searchTasks(String keyword) {
        List<Task> tasks = taskRepository.searchByKeyword(keyword);

        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> filterTasksByStatus(TaskStatus status) {
        List<Task> tasks = taskRepository.findByStatus(status);

        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> filterTasksByPriority(Priority priority) {
        List<Task> tasks = taskRepository.findByPriority(priority);

        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> searchTasksWithFilters(
            Long projectId,
            TaskStatus status,
            Priority priority,
            Long assignedToId,
            String keyword) {

        List<Task> tasks = taskRepository.searchTasksWithFilters(
                projectId, status, priority, assignedToId, keyword
        );

        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getOverdueTasks() {
        List<Task> tasks = taskRepository.findOverdueTasks(LocalDate.now());

        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskResponse> getMyOverdueTasks() {
        User currentUser = getCurrentUser();
        List<Task> tasks = taskRepository.findOverdueTasksForUser(
                currentUser.getId(),
                LocalDate.now()
        );

        return tasks.stream()
                .map(this::mapToTaskResponse)
                .collect(Collectors.toList());
    }


    // Helper methods
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private TaskResponse mapToTaskResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setStartDate(task.getStartDate());
        response.setDueDate(task.getDueDate());
        response.setCompletedAt(task.getCompletedAt());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());

        // Map project
        ProjectShortResponse projectShort = new ProjectShortResponse();
        projectShort.setId(task.getProject().getId());
        projectShort.setName(task.getProject().getName());
        response.setProject(projectShort);

        // Map assigned user
        if (task.getAssignedTo() != null) {
            UserSummary assignee = new UserSummary();
            assignee.setId(task.getAssignedTo().getId());
            assignee.setUsername(task.getAssignedTo().getUsername());
            assignee.setEmail(task.getAssignedTo().getEmail());
            response.setAssignedTo(assignee);
        }

        // Map creator
        UserSummary creator = new UserSummary();
        creator.setId(task.getCreatedBy().getId());
        creator.setUsername(task.getCreatedBy().getUsername());
        creator.setEmail(task.getCreatedBy().getEmail());
        response.setCreatedBy(creator);

        // Tags can be added later
        response.setTags(List.of());

        return response;
    }
    @Override
    public Page<TaskResponse> getAllTasksPaginated(Pageable pageable) {
        Page<Task> taskPage = taskRepository.findAll(pageable);
        return taskPage.map(this::mapToTaskResponse);
    }

    @Override
    public Page<TaskResponse> getTasksForProjectPaginated(Long projectId, Pageable pageable) {
        Page<Task> taskPage = taskRepository.findByProjectId(projectId, pageable);
        return taskPage.map(this::mapToTaskResponse);
    }

    @Override
    public Page<TaskResponse> getMyTasksPaginated(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Task> taskPage = taskRepository.findByAssignedToId(currentUser.getId(), pageable);
        return taskPage.map(this::mapToTaskResponse);
    }

    @Override
    public Page<TaskResponse> searchTasksPaginated(String keyword, Pageable pageable) {
        Page<Task> taskPage = taskRepository.searchByKeyword(keyword, pageable);
        return taskPage.map(this::mapToTaskResponse);
    }
}
