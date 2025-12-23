package com.project.task_manager.service;

import com.project.task_manager.dto.request.LoginRequest;
import com.project.task_manager.dto.request.RegisterRequest;
import com.project.task_manager.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
