package com.project.task_manager.controller;

import com.project.task_manager.config.swagger.*;
import com.project.task_manager.dto.request.RegisterRequest;
import com.project.task_manager.dto.request.RoleRequest;
import com.project.task_manager.dto.response.RoleResponse;
import com.project.task_manager.dto.response.UserResponse;
import com.project.task_manager.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    @GetMapping("/roles")
    @GetListApiResponses  // Adds 200, 401 responses (no 404 for lists)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all roles")
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        return ResponseEntity.ok(userService.getAllRoles());
    }

    @PostMapping("/roles")
    @PostApiResponses  // POST → 201, 400 (no 401 since it's public)
    @Operation(summary = "create new role")
    public ResponseEntity<RoleResponse> register(@Valid @RequestBody RoleRequest request) {
        RoleResponse response = userService.createRole(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/roles/{id}")
    @PutApiResponses  // POST → 201, 400 (no 401 since it's public)
    @Operation(summary = "update role")
    public void updateRole(@PathVariable Long id,@Valid @RequestBody RoleRequest request) {
        userService.updateRole(id,request);
    }

    @DeleteMapping("/roles/{id}")
    @Operation(summary = "Delete role")
    @DeleteApiResponses  // DELETE → 200, 404, 401
    public void deleteRole(
            @Parameter(description = "ID of the role to delete", required = true, example = "1")
            @PathVariable Long id) {
        userService.deleteRole(id);
    }

    @PutMapping("/{id}")
    @PutApiResponses  // POST → 201, 400 (no 401 since it's public)
    @Operation(summary = "update user")
    public void updateUser(@PathVariable Long id,@Valid @RequestBody RegisterRequest request) {
        userService.updateUser(id,request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    @DeleteApiResponses  // DELETE → 200, 404, 401
    public void deleteUser(
            @Parameter(description = "ID of the user to delete", required = true, example = "1")
            @PathVariable Long id) {
        userService.deleteUser(id);
    }
}
