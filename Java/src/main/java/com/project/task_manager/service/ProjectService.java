package com.project.task_manager.service;

import com.project.task_manager.dto.request.ProjectRequest;
import com.project.task_manager.dto.response.ProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ProjectService {
    ProjectResponse createProject(ProjectRequest request);
    ProjectResponse getProjectById(Long id);
    List<ProjectResponse> getProjectsForCurrentUser();
    ProjectResponse updateProject(Long id, ProjectRequest request);
    void deleteProject(Long id);
    Page<ProjectResponse> getAllProjectsPaginated(Pageable pageable);
    Page<ProjectResponse> getMyProjectsPaginated(Pageable pageable);

    // ADD THESE 3:
    void addTeamMember(Long projectId, Long userId, String role);
    void removeTeamMember(Long projectId, Long userId);
    List<Map<String, Object>> getProjectTeam(Long projectId);
}
