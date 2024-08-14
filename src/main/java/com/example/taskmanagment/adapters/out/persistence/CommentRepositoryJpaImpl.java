package com.example.taskmanagment.adapters.out.persistence;

import com.example.taskmanagment.application.domain.models.Comment;
import com.example.taskmanagment.application.domain.models.Task;
import com.example.taskmanagment.application.ports.out.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
@Qualifier("CommentRepoJpaImpl")
@Primary
public class CommentRepositoryJpaImpl implements CommentRepository {
    private final CommentJpaRepository commentJpaRepository;

    @Override
    public void save(Comment comment) {
        commentJpaRepository.save(comment);
    }

    @Override
    public Optional<Comment> findById(UUID commentId) {
        return commentJpaRepository.findById(commentId);
    }

    @Override
    public void delete(Comment comment) {
        commentJpaRepository.delete(comment);
    }

    @Override
    public List<Comment> findAllCommentsByTask(Task task) {
        return commentJpaRepository.findAllByTask(task);
    }
}
