package com.example.taskmanagment.application.ports.in;

import com.example.taskmanagment.adapters.in.rest.exceptions.PermissionDeniedException;
import com.example.taskmanagment.adapters.in.rest.exceptions.TaskNotFoundException;
import com.example.taskmanagment.adapters.in.rest.exceptions.UserNotFoundException;
import com.example.taskmanagment.application.domain.dto.TaskUpdateDTO;
import com.example.taskmanagment.application.domain.dto.TaskViewDTO;
import com.example.taskmanagment.application.domain.enums.PriorityLevel;
import com.example.taskmanagment.application.domain.enums.TaskStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface TaskService {

    void createTask(TaskUpdateDTO task);

    TaskViewDTO getTaskById(String id) throws TaskNotFoundException;

    TaskViewDTO updateTask(String taskId, TaskUpdateDTO task) throws TaskNotFoundException, UserNotFoundException, PermissionDeniedException;

    void deleteTask(String taskId) throws PermissionDeniedException;

    TaskViewDTO changeStatus(String taskId, TaskStatus newStatus);

    TaskViewDTO changeAssignee(String taskId, String assigneeEmail);

    List<TaskViewDTO> getTaskByAuthor(String userId, TaskStatus status, PriorityLevel priority, String page, String size);
    List<TaskViewDTO> getTaskByAssignee(String userId, TaskStatus status, PriorityLevel priority, String page, String size);
}
