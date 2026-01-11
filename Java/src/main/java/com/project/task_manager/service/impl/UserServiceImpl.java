package com.project.task_manager.service.impl;

import com.project.task_manager.constants.RoleConstants;
import com.project.task_manager.dto.request.RegisterRequest;
import com.project.task_manager.dto.request.RoleRequest;
import com.project.task_manager.dto.response.RoleResponse;
import com.project.task_manager.dto.response.UserResponse;
import com.project.task_manager.exception.BadRequestException;
import com.project.task_manager.exception.ResourceNotFoundException;
import com.project.task_manager.model.Role;
import com.project.task_manager.model.User;
import com.project.task_manager.repository.RoleRepository;
import com.project.task_manager.repository.UserRepository;
import com.project.task_manager.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final EntityManager entityManager;

    private final RoleRepository roleRepository;

    @Override
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToUserResponse(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return mapToUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleResponse> getAllRoles() {
        List<RoleResponse> allUsers = userRepository.findAllRoles();
        return allUsers;
    }

    private UserResponse mapToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setPhone(user.getPhone());
        response.setProfileImage(user.getProfileImage());
        response.setIsActive(user.getIsActive());
        response.setIsVerified(user.getIsVerified());
        response.setRoles(user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet()));
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        entityManager.persist(role);
        entityManager.flush();
        return mapToRoleResponse(role);
    }

    @Override
    @Transactional
    public void updateRole(Long id, RoleRequest request) {
        Role role = entityManager.find(Role.class, id);
        if (role == null) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        entityManager.merge(role);
        // No need for flush() - merge() handles it
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = entityManager.find(Role.class, id);
        if (role == null) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        entityManager.remove(role);
    }

    private RoleResponse mapToRoleResponse(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        return response;
    }

    @Override
    @Transactional
    public void updateUser(Long id,RegisterRequest request) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());

        // Handle multiple roles
        Set<Role> userRoles = new HashSet<>();

        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            // Default role if none provided
            Role defaultRole = roleRepository.findByName(RoleConstants.MEMBER)
                    .orElseThrow(() -> new RuntimeException("Default role not found: " + RoleConstants.MEMBER));
            userRoles.add(defaultRole);
        } else {
            // Fetch all requested roles
            for (String roleName : request.getRoles()) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new BadRequestException("Role not found: " + roleName));
                userRoles.add(role);
            }
        }

        user.setRoles(userRoles);
        user.setIsActive(true);
        user.setIsVerified(false);
        entityManager.merge(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        entityManager.remove(user);
    }
}
