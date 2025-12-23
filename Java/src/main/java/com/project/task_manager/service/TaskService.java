package com.project.task_manager.service;

import com.project.task_manager.dto.request.TaskRequest;
import com.project.task_manager.dto.response.TaskResponse;
import com.project.task_manager.enums.Priority;
import com.project.task_manager.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TaskService {
    TaskResponse createTask(TaskRequest request);
    TaskResponse getTaskById(Long id);
    List<TaskResponse> getTasksForProject(Long projectId);
    List<TaskResponse> getTasksForCurrentUser();
    TaskResponse updateTask(Long id, TaskRequest request);
    void deleteTask(Long id);

    List<TaskResponse> searchTasks(String keyword);

    List<TaskResponse> filterTasksByStatus(TaskStatus status);

    List<TaskResponse> filterTasksByPriority(Priority priority);

    List<TaskResponse> searchTasksWithFilters(
            Long projectId,
            TaskStatus status,
            Priority priority,
            Long assignedToId,
            String keyword
    );

    List<TaskResponse> getOverdueTasks();

    List<TaskResponse> getMyOverdueTasks();
    // NEW: Paginated methods
    Page<TaskResponse> getAllTasksPaginated(Pageable pageable);

    Page<TaskResponse> getTasksForProjectPaginated(Long projectId, Pageable pageable);

    Page<TaskResponse> getMyTasksPaginated(Pageable pageable);

    Page<TaskResponse> searchTasksPaginated(String keyword, Pageable pageable);
}
