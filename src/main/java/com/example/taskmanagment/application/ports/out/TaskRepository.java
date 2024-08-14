package com.example.taskmanagment.application.ports.out;

import com.example.taskmanagment.application.domain.models.Task;
import com.example.taskmanagment.application.domain.models.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository {
     void save(Task task);
     Optional<Task> findById(UUID id);

    void delete(Task taskToDelete);

    List<Task> findAllByAuthor(User user);

    List<Task> findAllByAssignee(User user);
}
