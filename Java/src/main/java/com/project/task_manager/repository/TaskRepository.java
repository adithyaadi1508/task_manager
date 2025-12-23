package com.project.task_manager.repository;

import com.project.task_manager.model.Task;
import com.project.task_manager.enums.TaskStatus;
import com.project.task_manager.enums.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find tasks by project
    List<Task> findByProjectId(Long projectId);

    // Find tasks assigned to a user
    List<Task> findByAssignedToId(Long userId);

    // Find tasks by status
    List<Task> findByStatus(TaskStatus status);

    // Find tasks by priority
    List<Task> findByPriority(Priority priority);

    // Find overdue tasks
    @Query("SELECT t FROM Task t WHERE t.dueDate < :currentDate AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDate currentDate);

    // Find tasks created by user
    List<Task> findByCreatedById(Long userId);

    // Search tasks by title
    List<Task> findByTitleContainingIgnoreCase(String title);

    // Find tasks with details (with relationships)
    @Query("SELECT t FROM Task t " +
            "LEFT JOIN FETCH t.assignedTo " +
            "LEFT JOIN FETCH t.project " +
            "WHERE t.id = :id")
    Task findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Task> searchByKeyword(@Param("keyword") String keyword);

    // Filter by status and priority
    List<Task> findByStatusAndPriority(TaskStatus status, Priority priority);

    // Find tasks due between dates
    List<Task> findByDueDateBetween(LocalDate startDate, LocalDate endDate);

    // Complex search with multiple optional filters
    @Query("SELECT DISTINCT t FROM Task t " +
            "LEFT JOIN t.project p " +
            "LEFT JOIN t.assignedTo u " +
            "WHERE (:projectId IS NULL OR p.id = :projectId) " +
            "AND (:status IS NULL OR t.status = :status) " +
            "AND (:priority IS NULL OR t.priority = :priority) " +
            "AND (:assignedToId IS NULL OR u.id = :assignedToId) " +
            "AND (:keyword IS NULL OR :keyword = '' OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Task> searchTasksWithFilters(
            @Param("projectId") Long projectId,
            @Param("status") TaskStatus status,
            @Param("priority") Priority priority,
            @Param("assignedToId") Long assignedToId,
            @Param("keyword") String keyword
    );


    // Find overdue tasks for a specific user
    @Query("SELECT t FROM Task t WHERE t.assignedTo.id = :userId " +
            "AND t.dueDate < :currentDate AND t.status != 'COMPLETED'")
    List<Task> findOverdueTasksForUser(
            @Param("userId") Long userId,
            @Param("currentDate") LocalDate currentDate
    );

    // Paginated queries
    Page<Task> findByProjectId(Long projectId, Pageable pageable);

    Page<Task> findByAssignedToId(Long userId, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Task> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
