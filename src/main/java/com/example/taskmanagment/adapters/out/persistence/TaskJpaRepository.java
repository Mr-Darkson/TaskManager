package com.example.taskmanagment.adapters.out.persistence;

import com.example.taskmanagment.application.domain.models.Task;
import com.example.taskmanagment.application.domain.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskJpaRepository extends JpaRepository<Task, UUID> {
    List<Task> findAllByAuthor(User author);
    List<Task> findAllByAssignee(User user);
}
