package com.project.task_manager.service.impl;

import com.project.task_manager.dto.request.CommentRequest;
import com.project.task_manager.dto.response.CommentResponse;
import com.project.task_manager.dto.response.UserSummary;
import com.project.task_manager.exception.BadRequestException;
import com.project.task_manager.exception.ResourceNotFoundException;
import com.project.task_manager.model.Comment;
import com.project.task_manager.model.Task;
import com.project.task_manager.model.User;
import com.project.task_manager.repository.CommentRepository;
import com.project.task_manager.repository.TaskRepository;
import com.project.task_manager.repository.UserRepository;
import com.project.task_manager.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CommentResponse addComment(CommentRequest request) {
        User currentUser = getCurrentUser();

        // Validate task exists
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Create comment
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setTask(task);
        comment.setUser(currentUser);
        comment.setIsEdited(false);

        // Handle parent comment (threaded replies)
        if (request.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }

        Comment savedComment = commentRepository.save(comment);

        return mapToCommentResponse(savedComment);
    }

    @Override
    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        return mapToCommentResponse(comment);
    }

    @Override
    public List<CommentResponse> getCommentsForTask(Long taskId) {
        List<Comment> comments = commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);

        return comments.stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long id, CommentRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User currentUser = getCurrentUser();

        // Check if current user is comment author
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only edit your own comments");
        }

        comment.setContent(request.getContent());
        comment.setIsEdited(true);

        Comment updatedComment = commentRepository.save(comment);

        return mapToCommentResponse(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User currentUser = getCurrentUser();

        // Check if current user is comment author
        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    // Helper methods
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private CommentResponse mapToCommentResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setTaskId(comment.getTask().getId());
        response.setContent(comment.getContent());
        response.setIsEdited(comment.getIsEdited());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());

        // Map user
        UserSummary userSummary = new UserSummary();
        userSummary.setId(comment.getUser().getId());
        userSummary.setUsername(comment.getUser().getUsername());
        userSummary.setEmail(comment.getUser().getEmail());
        response.setUser(userSummary);

        // Parent comment ID (for threaded replies)
        if (comment.getParentComment() != null) {
            response.setParentCommentId(comment.getParentComment().getId());
        }

        // Replies (optional - can load separately)
        response.setReplies(List.of());

        return response;
    }
}
