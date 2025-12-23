package com.project.task_manager.service.impl;

import com.project.task_manager.dto.request.ProjectRequest;
import com.project.task_manager.dto.response.ProjectResponse;
import com.project.task_manager.dto.response.UserSummary;
import com.project.task_manager.enums.Priority;
import com.project.task_manager.enums.ProjectStatus;
import com.project.task_manager.exception.BadRequestException;
import com.project.task_manager.exception.ResourceNotFoundException;
import com.project.task_manager.model.Project;
import com.project.task_manager.model.ProjectMember;
import com.project.task_manager.model.User;
import com.project.task_manager.repository.ProjectMemberRepository;
import com.project.task_manager.repository.ProjectRepository;
import com.project.task_manager.repository.UserRepository;
import com.project.task_manager.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;


    @Override
    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Project name is required");
        }
        if (request.getStartDate() == null) {
            throw new BadRequestException("Start date is required");
        }
        // Get current logged-in user
        User currentUser = getCurrentUser();

        // Create project
        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());
        project.setStatus(request.getStatus() != null ? request.getStatus() : ProjectStatus.PLANNING);
        project.setPriority(request.getPriority() != null ? request.getPriority() : Priority.MEDIUM);
        project.setOwner(currentUser);
        project.setCreatedBy(currentUser);

        // Save project
        Project savedProject = projectRepository.save(project);

        return mapToProjectResponse(savedProject);
    }

    @Override
    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        return mapToProjectResponse(project);
    }

    @Override
    public List<ProjectResponse> getProjectsForCurrentUser() {
        User currentUser = getCurrentUser();

        // Get all projects where user is owner
        List<Project> projects = projectRepository.findByOwnerId(currentUser.getId());

        return projects.stream()
                .map(this::mapToProjectResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        User currentUser = getCurrentUser();

        // Check if current user is owner
        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You don't have permission to update this project");
        }

        // Update fields
        if (request.getName() != null) {
            project.setName(request.getName());
        }
        if (request.getDescription() != null) {
            project.setDescription(request.getDescription());
        }
        if (request.getStartDate() != null) {
            project.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            project.setEndDate(request.getEndDate());
        }
        if (request.getStatus() != null) {
            project.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            project.setPriority(request.getPriority());
        }

        project.setUpdatedBy(currentUser);

        Project updatedProject = projectRepository.save(project);

        return mapToProjectResponse(updatedProject);
    }

    @Override
    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + id));

        User currentUser = getCurrentUser();

        // Check if current user is owner
        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You don't have permission to delete this project");
        }

        projectRepository.delete(project);
    }

    // Helper methods
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private ProjectResponse mapToProjectResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setStatus(project.getStatus());
        response.setPriority(project.getPriority());
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());
        response.setCreatedAt(project.getCreatedAt());
        response.setUpdatedAt(project.getUpdatedAt());

        // Map owner
        UserSummary ownerSummary = new UserSummary();
        ownerSummary.setId(project.getOwner().getId());
        ownerSummary.setUsername(project.getOwner().getUsername());
        ownerSummary.setEmail(project.getOwner().getEmail());
        response.setOwner(ownerSummary);

        // Members can be added later
        response.setMembers(List.of());

        return response;
    }

    @Override
    public Page<ProjectResponse> getAllProjectsPaginated(Pageable pageable) {
        Page<Project> projectPage = projectRepository.findAll(pageable);
        return projectPage.map(this::mapToProjectResponse);
    }

    @Override
    public Page<ProjectResponse> getMyProjectsPaginated(Pageable pageable) {
        User currentUser = getCurrentUser();
        Page<Project> projectPage = projectRepository.findByOwnerId(currentUser.getId(), pageable);
        return projectPage.map(this::mapToProjectResponse);
    }
    @Override
    @Transactional
    public void addTeamMember(Long projectId, Long userId, String role) {
        // Check if project exists
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        // Check if user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // Check if already a member
        Long count = projectMemberRepository.countByProjectIdAndUserId(projectId, userId);
        if (count > 0) {
            throw new BadRequestException("User is already a team member");
        }

        // Insert directly using native query
        projectMemberRepository.insertTeamMember(projectId, userId, role, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void removeTeamMember(Long projectId, Long userId) {
        Long count = projectMemberRepository.countByProjectIdAndUserId(projectId, userId);
        if (count == 0) {
            throw new ResourceNotFoundException("User is not a team member");
        }
        projectMemberRepository.deleteByProjectIdAndUserId(projectId, userId);
    }


    @Override
    public List<Map<String, Object>> getProjectTeam(Long projectId) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        List<ProjectMember> members = projectMemberRepository.findByProjectIdWithUsers(projectId);

        return members.stream().map(member -> {
            User user = userRepository.findById(member.getUserId()).orElse(null);

            Map<String, Object> memberData = new HashMap<>();
            memberData.put("userId", member.getUserId());
            memberData.put("role", member.getRole());
            memberData.put("joinedAt", member.getJoinedAt());
            if (user != null) {
                memberData.put("username", user.getUsername());
                memberData.put("email", user.getEmail());
            }
            return memberData;
        }).collect(Collectors.toList());
    }

}