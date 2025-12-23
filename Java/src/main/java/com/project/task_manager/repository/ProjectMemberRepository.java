package com.project.task_manager.repository;

import com.project.task_manager.model.ProjectMember;
import com.project.task_manager.model.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

    @Query(value = "SELECT * FROM admin_schema.project_members WHERE project_id = :projectId", nativeQuery = true)
    List<ProjectMember> findByProjectId(@Param("projectId") Long projectId);

    @Query(value = "SELECT COUNT(*) FROM admin_schema.project_members WHERE project_id = :projectId AND user_id = :userId", nativeQuery = true)
    Long countByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    @Modifying
    @Query(value = "INSERT INTO admin_schema.project_members (project_id, user_id, role, joined_at) VALUES (:projectId, :userId, :role, :joinedAt)", nativeQuery = true)
    void insertTeamMember(@Param("projectId") Long projectId,
                          @Param("userId") Long userId,
                          @Param("role") String role,
                          @Param("joinedAt") LocalDateTime joinedAt);

    @Modifying
    @Query(value = "DELETE FROM admin_schema.project_members WHERE project_id = :projectId AND user_id = :userId", nativeQuery = true)
    void deleteByProjectIdAndUserId(@Param("projectId") Long projectId, @Param("userId") Long userId);

    @Query(value = "SELECT * FROM admin_schema.project_members WHERE project_id = :projectId", nativeQuery = true)
    List<ProjectMember> findByProjectIdWithUsers(@Param("projectId") Long projectId);
}
