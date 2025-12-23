package com.project.task_manager.service;

import com.project.task_manager.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse getCurrentUser();
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
}
