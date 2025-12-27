package com.project.task_manager.controller;

import com.project.task_manager.config.swagger.PostApiResponses;
import com.project.task_manager.dto.request.LoginRequest;
import com.project.task_manager.dto.request.RegisterRequest;
import com.project.task_manager.dto.response.AuthResponse;
import com.project.task_manager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication & Authorization", description = "APIs for user registration, login, and JWT token management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @PostApiResponses  // POST → 201, 400 (no 401 since it's public)
    @Operation(summary = "Register new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @PostApiResponses  // POST → 200, 400 (no 401 since it's public)
    @Operation(summary = "User login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
