package com.project.task_manager.controller;

import com.project.task_manager.config.swagger.GetApiResponses;
import com.project.task_manager.config.swagger.GetListApiResponses;
import com.project.task_manager.config.swagger.StandardApiResponses;
import com.project.task_manager.dto.response.UserResponse;
import com.project.task_manager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for user profile and information management")
@StandardApiResponses  // Replaces @SecurityRequirement - now applied via annotation
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @StandardApiResponses  // Only 200 + 401 responses needed
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @GetMapping("/{id}")
    @GetApiResponses  // Adds 200, 401, 404 responses
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID of the user to retrieve", example = "2")
            @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping
    @GetListApiResponses  // Adds 200, 401 responses (no 404 for lists)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'MEMBER')")
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
