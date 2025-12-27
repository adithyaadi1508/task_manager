package com.project.task_manager.controller;

import com.project.task_manager.config.swagger.*;
import com.project.task_manager.dto.request.CommentRequest;
import com.project.task_manager.dto.response.CommentResponse;
import com.project.task_manager.dto.response.MessageResponse;
import com.project.task_manager.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@Tag(name = "Comment Management", description = "APIs for managing task comments and discussions")
@StandardApiResponses
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @Operation(summary = "Add comment to task")
    @PostApiResponses  // POST = Create → 201, 400, 401
    public ResponseEntity<CommentResponse> addComment(@Valid @RequestBody CommentRequest request) {
        CommentResponse response = commentService.addComment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get comment by ID")
    @GetApiResponses  // GET by ID → 200, 404, 401
    public ResponseEntity<CommentResponse> getCommentById(
            @Parameter(description = "ID of the comment to retrieve", required = true, example = "1")
            @PathVariable Long id) {
        CommentResponse response = commentService.getCommentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get all comments for a task")
    @GetListApiResponses  // GET list → 200, 401
    public ResponseEntity<List<CommentResponse>> getCommentsForTask(
            @Parameter(description = "ID of the task to retrieve comments for", required = true, example = "1")
            @PathVariable Long taskId) {
        List<CommentResponse> comments = commentService.getCommentsForTask(taskId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update comment")
    @PutApiResponses  // PUT = Update → 200, 400, 404, 401
    public ResponseEntity<CommentResponse> updateComment(
            @Parameter(description = "ID of the comment to update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest request) {
        CommentResponse response = commentService.updateComment(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete comment")
    @DeleteApiResponses  // DELETE → 200, 404, 401
    public ResponseEntity<MessageResponse> deleteComment(
            @Parameter(description = "ID of the comment to delete", required = true, example = "1")
            @PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok(new MessageResponse(true, "Comment deleted successfully"));
    }
}
