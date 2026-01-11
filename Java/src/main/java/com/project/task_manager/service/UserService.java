package com.project.task_manager.service;

import com.project.task_manager.dto.request.RegisterRequest;
import com.project.task_manager.dto.request.RoleRequest;
import com.project.task_manager.dto.response.RoleResponse;
import com.project.task_manager.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse getCurrentUser();
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    List<RoleResponse> getAllRoles();
    RoleResponse createRole(RoleRequest request);
    void updateRole(Long id,RoleRequest request);
    void deleteRole(Long id);
    void updateUser(Long id, RegisterRequest request);
    void deleteUser(Long id);
}
