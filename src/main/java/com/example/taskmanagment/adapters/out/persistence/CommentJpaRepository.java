package com.example.taskmanagment.adapters.out.persistence;

import com.example.taskmanagment.application.domain.models.Comment;
import com.example.taskmanagment.application.domain.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.UUID;
public interface CommentJpaRepository extends JpaRepository<Comment, UUID> {

    List<Comment> findAllByTask(Task task);
}
