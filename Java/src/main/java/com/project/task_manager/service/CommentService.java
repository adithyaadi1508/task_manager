package com.project.task_manager.service;

import com.project.task_manager.dto.request.CommentRequest;
import com.project.task_manager.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse addComment(CommentRequest request);

    CommentResponse getCommentById(Long id);

    List<CommentResponse> getCommentsForTask(Long taskId);

    CommentResponse updateComment(Long id, CommentRequest request);

    void deleteComment(Long id);
}
