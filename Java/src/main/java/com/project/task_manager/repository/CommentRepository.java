package com.project.task_manager.repository;

import com.project.task_manager.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Find all comments for a task, ordered by newest first
    List<Comment> findByTaskIdOrderByCreatedAtDesc(Long taskId);

    // Find comments by user
    List<Comment> findByUserId(Long userId);

    // Find replies to a comment
    List<Comment> findByParentCommentId(Long parentCommentId);
}
