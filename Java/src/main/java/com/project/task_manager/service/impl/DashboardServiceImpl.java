package com.project.task_manager.service.impl;

import com.project.task_manager.dto.response.DashboardStatsResponse;
import com.project.task_manager.dto.response.ProjectStatsResponse;
import com.project.task_manager.dto.response.UserWorkloadResponse;
import com.project.task_manager.enums.Priority;
import com.project.task_manager.enums.ProjectStatus;
import com.project.task_manager.enums.TaskStatus;
import com.project.task_manager.exception.ResourceNotFoundException;
import com.project.task_manager.model.Project;
import com.project.task_manager.model.Task;
import com.project.task_manager.model.User;
import com.project.task_manager.repository.ProjectRepository;
import com.project.task_manager.repository.TaskRepository;
import com.project.task_manager.repository.UserRepository;
import com.project.task_manager.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardStatsResponse getOverallStats() {
        DashboardStatsResponse stats = new DashboardStatsResponse();

        // Get all tasks
        List<Task> allTasks = taskRepository.findAll();

        // Total tasks
        stats.setTotalTasks((long) allTasks.size());

        // Count by status
        long completed = allTasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        long inProgress = allTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long todo = allTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();

        stats.setCompletedTasks(completed);
        stats.setInProgressTasks(inProgress);
        stats.setTodoTasks(todo);

        // Overdue tasks
        LocalDate today = LocalDate.now();
        long overdue = allTasks.stream()
                .filter(t -> t.getDueDate() != null)
                .filter(t -> t.getDueDate().isBefore(today))
                .filter(t -> t.getStatus() != TaskStatus.COMPLETED)
                .count();
        stats.setOverdueTasksCount(overdue);

        // Completion rate
        double completionRate = allTasks.isEmpty() ? 0.0 : (completed * 100.0) / allTasks.size();
        stats.setCompletionRate(Math.round(completionRate * 100.0) / 100.0);

        // Project statistics
        List<Project> allProjects = projectRepository.findAll();
        stats.setTotalProjects((long) allProjects.size());
        long activeProjects = allProjects.stream()
                .filter(p -> p.getStatus() == ProjectStatus.IN_PROGRESS || p.getStatus() == ProjectStatus.PLANNING)
                .count();
        stats.setActiveProjects(activeProjects);

        // Tasks by priority
        Map<String, Long> tasksByPriority = new HashMap<>();
        for (Priority priority : Priority.values()) {
            long count = allTasks.stream().filter(t -> t.getPriority() == priority).count();
            tasksByPriority.put(priority.name(), count);
        }
        stats.setTasksByPriority(tasksByPriority);

        // Tasks by status
        Map<String, Long> tasksByStatus = new HashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            long count = allTasks.stream().filter(t -> t.getStatus() == status).count();
            tasksByStatus.put(status.name(), count);
        }
        stats.setTasksByStatus(tasksByStatus);

        return stats;
    }

    @Override
    public DashboardStatsResponse getMyDashboardStats() {
        User currentUser = getCurrentUser();
        DashboardStatsResponse stats = new DashboardStatsResponse();

        // Get tasks assigned to current user
        List<Task> myTasks = taskRepository.findByAssignedToId(currentUser.getId());

        // Total tasks
        stats.setTotalTasks((long) myTasks.size());

        // Count by status
        long completed = myTasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        long inProgress = myTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long todo = myTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();

        stats.setCompletedTasks(completed);
        stats.setInProgressTasks(inProgress);
        stats.setTodoTasks(todo);

        // Overdue tasks
        LocalDate today = LocalDate.now();
        long overdue = myTasks.stream()
                .filter(t -> t.getDueDate() != null)
                .filter(t -> t.getDueDate().isBefore(today))
                .filter(t -> t.getStatus() != TaskStatus.COMPLETED)
                .count();
        stats.setOverdueTasksCount(overdue);

        // Completion rate
        double completionRate = myTasks.isEmpty() ? 0.0 : (completed * 100.0) / myTasks.size();
        stats.setCompletionRate(Math.round(completionRate * 100.0) / 100.0);

        // My projects (where I'm owner)
        List<Project> myProjects = projectRepository.findByOwnerId(currentUser.getId());
        stats.setTotalProjects((long) myProjects.size());
        long activeProjects = myProjects.stream()
                .filter(p -> p.getStatus() == ProjectStatus.IN_PROGRESS || p.getStatus() == ProjectStatus.PLANNING)
                .count();
        stats.setActiveProjects(activeProjects);

        // Tasks by priority
        Map<String, Long> tasksByPriority = new HashMap<>();
        for (Priority priority : Priority.values()) {
            long count = myTasks.stream().filter(t -> t.getPriority() == priority).count();
            tasksByPriority.put(priority.name(), count);
        }
        stats.setTasksByPriority(tasksByPriority);

        // Tasks by status
        Map<String, Long> tasksByStatus = new HashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            long count = myTasks.stream().filter(t -> t.getStatus() == status).count();
            tasksByStatus.put(status.name(), count);
        }
        stats.setTasksByStatus(tasksByStatus);

        return stats;
    }

    @Override
    public ProjectStatsResponse getProjectStats(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        ProjectStatsResponse stats = new ProjectStatsResponse();
        stats.setProjectId(project.getId());
        stats.setProjectName(project.getName());

        // Get all tasks for this project
        List<Task> projectTasks = taskRepository.findByProjectId(projectId);

        // Total tasks
        stats.setTotalTasks((long) projectTasks.size());

        // Count by status
        long completed = projectTasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        long inProgress = projectTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long todo = projectTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();

        stats.setCompletedTasks(completed);
        stats.setInProgressTasks(inProgress);
        stats.setTodoTasks(todo);

        // Overdue tasks
        LocalDate today = LocalDate.now();
        long overdue = projectTasks.stream()
                .filter(t -> t.getDueDate() != null)
                .filter(t -> t.getDueDate().isBefore(today))
                .filter(t -> t.getStatus() != TaskStatus.COMPLETED)
                .count();
        stats.setOverdueTasksCount(overdue);

        // Completion rate
        double completionRate = projectTasks.isEmpty() ? 0.0 : (completed * 100.0) / projectTasks.size();
        stats.setCompletionRate(Math.round(completionRate * 100.0) / 100.0);

        // Tasks by priority
        Map<String, Long> tasksByPriority = new HashMap<>();
        for (Priority priority : Priority.values()) {
            long count = projectTasks.stream().filter(t -> t.getPriority() == priority).count();
            tasksByPriority.put(priority.name(), count);
        }
        stats.setTasksByPriority(tasksByPriority);

        // Tasks by status
        Map<String, Long> tasksByStatus = new HashMap<>();
        for (TaskStatus status : TaskStatus.values()) {
            long count = projectTasks.stream().filter(t -> t.getStatus() == status).count();
            tasksByStatus.put(status.name(), count);
        }
        stats.setTasksByStatus(tasksByStatus);

        return stats;
    }

    @Override
    public UserWorkloadResponse getMyWorkload() {
        User currentUser = getCurrentUser();
        return getUserWorkload(currentUser.getId());
    }

    @Override
    public UserWorkloadResponse getUserWorkload(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserWorkloadResponse workload = new UserWorkloadResponse();
        workload.setUserId(user.getId());
        workload.setUsername(user.getUsername());

        // Get all assigned tasks
        List<Task> assignedTasks = taskRepository.findByAssignedToId(userId);

        // Total assigned
        workload.setTotalAssignedTasks((long) assignedTasks.size());

        // Count by status
        long completed = assignedTasks.stream().filter(t -> t.getStatus() == TaskStatus.COMPLETED).count();
        long inProgress = assignedTasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long todo = assignedTasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();

        workload.setCompletedTasks(completed);
        workload.setInProgressTasks(inProgress);
        workload.setTodoTasks(todo);

        // Overdue tasks
        LocalDate today = LocalDate.now();
        long overdue = assignedTasks.stream()
                .filter(t -> t.getDueDate() != null)
                .filter(t -> t.getDueDate().isBefore(today))
                .filter(t -> t.getStatus() != TaskStatus.COMPLETED)
                .count();
        workload.setOverdueTasksCount(overdue);

        // Completion rate
        double completionRate = assignedTasks.isEmpty() ? 0.0 : (completed * 100.0) / assignedTasks.size();
        workload.setCompletionRate(Math.round(completionRate * 100.0) / 100.0);

        // Tasks completed this week
        Instant weekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        int completedThisWeek = (int) assignedTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .filter(t -> t.getCompletedAt() != null)
                .filter(t -> t.getCompletedAt().isAfter(weekAgo))
                .count();
        workload.setTasksCompletedThisWeek(completedThisWeek);

        // Tasks completed this month
        Instant monthAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        int completedThisMonth = (int) assignedTasks.stream()
                .filter(t -> t.getStatus() == TaskStatus.COMPLETED)
                .filter(t -> t.getCompletedAt() != null)
                .filter(t -> t.getCompletedAt().isAfter(monthAgo))
                .count();
        workload.setTasksCompletedThisMonth(completedThisMonth);

        return workload;
    }

    // Helper method
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
