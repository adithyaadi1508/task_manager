package com.project.task_manager.repository;

import com.project.task_manager.model.Project;
import com.project.task_manager.enums.ProjectStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Find all projects owned by a user
    List<Project> findByOwnerId(Long ownerId);

    // Find projects by status
    List<Project> findByStatus(ProjectStatus status);

    // Find projects where user is owner or member
    @Query(value = "SELECT DISTINCT p.* FROM admin_schema.projects p " +
            "LEFT JOIN admin_schema.project_members pm ON pm.project_id = p.id " +
            "WHERE p.owner_id = :userId OR pm.user_id = :userId",
            nativeQuery = true)
    List<Project> findAllUserProjects(@Param("userId") Long userId);

    // Search projects by name
    List<Project> findByNameContainingIgnoreCase(String name);
    Page<Project> findByOwnerId(Long ownerId, Pageable pageable);

}
