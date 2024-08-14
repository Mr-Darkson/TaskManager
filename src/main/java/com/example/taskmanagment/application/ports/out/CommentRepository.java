package com.example.taskmanagment.application.ports.out;

import com.example.taskmanagment.application.domain.models.Comment;
import com.example.taskmanagment.application.domain.models.Task;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository {
    void save(Comment comment);

    Optional<Comment> findById(UUID commentId);

    void delete(Comment comment);

    List<Comment> findAllCommentsByTask(Task task);
}
