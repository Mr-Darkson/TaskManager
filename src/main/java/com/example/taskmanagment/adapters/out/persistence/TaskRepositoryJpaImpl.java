package com.example.taskmanagment.adapters.out.persistence;

import com.example.taskmanagment.application.domain.models.Task;
import com.example.taskmanagment.application.domain.models.User;
import com.example.taskmanagment.application.ports.out.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
@Qualifier("TaskRepoJpaImpl")
@Primary
public class TaskRepositoryJpaImpl implements TaskRepository {
    private final TaskJpaRepository taskJpaRepository;

    @Override
    public void save(Task task) {
        taskJpaRepository.save(task);
    }

    @Override
    public Optional<Task> findById(UUID id) {
        return taskJpaRepository.findById(id);
    }

    @Override
    public void delete(Task taskToDelete) {
        taskJpaRepository.delete(taskToDelete);
    }

    @Override
    public List<Task> findAllByAuthor(User user) {
        return taskJpaRepository.findAllByAuthor(user);
    }

    @Override
    public List<Task> findAllByAssignee(User user) {
        return taskJpaRepository.findAllByAssignee(user);
    }
}
