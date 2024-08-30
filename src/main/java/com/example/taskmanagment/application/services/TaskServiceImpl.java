package com.example.taskmanagment.application.services;

import com.example.taskmanagment.adapters.in.rest.exceptions.PermissionDeniedException;
import com.example.taskmanagment.adapters.in.rest.exceptions.TaskNotFoundException;
import com.example.taskmanagment.adapters.in.rest.exceptions.UserNotFoundException;
import com.example.taskmanagment.adapters.in.security.CustomUserDetails;
import com.example.taskmanagment.application.domain.dto.TaskUpdateDTO;
import com.example.taskmanagment.application.domain.dto.TaskViewDTO;
import com.example.taskmanagment.application.domain.enums.PriorityLevel;
import com.example.taskmanagment.application.domain.enums.TaskStatus;
import com.example.taskmanagment.application.domain.models.Task;
import com.example.taskmanagment.application.domain.models.User;
import com.example.taskmanagment.application.ports.in.TaskService;
import com.example.taskmanagment.application.ports.out.TaskRepository;
import com.example.taskmanagment.application.ports.out.UserRepository;
import com.example.taskmanagment.application.utils.mappers.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы с задачами
 */
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;


    /**
     * Создаёт новую задачу по данным с DTO-обновления
     * @param taskUpdateDTO DTO-обновление
     */
    @Override
    public void createTask(TaskUpdateDTO taskUpdateDTO) {
        User authenticatedUser = getUserFromSecurityContext();
        Task newTask = taskMapper.toTask(taskUpdateDTO);

        if (taskUpdateDTO.getAssigneeEmail() != null) {
            User assignee = userRepository.findByEmail(taskUpdateDTO.getAssigneeEmail())
                    .orElseThrow(() -> new UserNotFoundException("Assignee not found"));
            newTask.setAssignee(assignee);
        }

        newTask.setAuthor(authenticatedUser);
        newTask.setCreatedDate(LocalDateTime.now());
        newTask.setUpdatedAt(LocalDateTime.now());

        taskRepository.save(newTask);

    }


    /**
     * Получить задачу по её ID
     * @param id идентификатор задачи
     * @return TaskViewDTO DTO-отображение
     * @throws TaskNotFoundException выбрасывается, если не найдена задача
     */
    @Override
    public TaskViewDTO getTaskById(String id) throws TaskNotFoundException {
        Task task = taskRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        return taskMapper.toTaskDto(task);
    }

    /**
     * Получает на вход ID задачи и её DTO-обновление. В задаче, с переданным ID, поля заменяются на те, что переданы в DTO и не равны null
     * @param taskId идентификатор задачи
     * @param update DTO-обновление
     * @return TaskViewDTO DTO-отображение
     * @throws TaskNotFoundException выбрасывается, если задача не найдена
     * @throws UserNotFoundException выбрасывается, если не найден новый человек, назначенный на роль исполнителя
     * @throws PermissionDeniedException выбрасывается, если у пользователя нет прав на выполнение данной команды
     */
    @Override
    public TaskViewDTO updateTask(String taskId, TaskUpdateDTO update) throws TaskNotFoundException, UserNotFoundException, PermissionDeniedException {
        User authenticatedUser = getUserFromSecurityContext();
        Task taskToUpdate = taskRepository.findById(UUID.fromString(taskId)).orElseThrow(TaskNotFoundException::new);

        if (taskToUpdate.getAuthor().equals(authenticatedUser)) {
            updateMainTaskInfo(taskToUpdate, update);
        } else throw new PermissionDeniedException();

        taskRepository.save(taskToUpdate);

        return taskMapper.toTaskDto(taskToUpdate);
    }

    /**
     * @param taskId идентификатор задачи
     * @param newStatus новый статус задачи
     * @return
     */
    @Override
    public TaskViewDTO changeStatus(String taskId, TaskStatus newStatus) {
        User authenticatedUser = getUserFromSecurityContext();
        Task taskToUpdate = taskRepository.findById(UUID.fromString(taskId)).orElseThrow(TaskNotFoundException::new);
        if (taskToUpdate.getAuthor().equals(authenticatedUser) || taskToUpdate.getAssignee().equals(authenticatedUser)) {
            taskToUpdate.setStatus(newStatus);
            taskRepository.save(taskToUpdate);

            return taskMapper.toTaskDto(taskToUpdate);
        } else throw new PermissionDeniedException();
    }

    @Override
    public TaskViewDTO changeAssignee(String taskId, String assigneeEmail) {
        User authenticatedUser = getUserFromSecurityContext();
        Task taskToUpdate = taskRepository.findById(UUID.fromString(taskId)).orElseThrow(TaskNotFoundException::new);
        if (taskToUpdate.getAuthor().equals(authenticatedUser)) {
            User newAssignee = userRepository.findByEmail(assigneeEmail).orElseThrow(UserNotFoundException::new);

            taskToUpdate.setAssignee(newAssignee);
            taskRepository.save(taskToUpdate);

            return taskMapper.toTaskDto(taskToUpdate);
        } else throw new PermissionDeniedException();
    }


    @Override
    public void deleteTask(String taskId) {
        User authenticatedUser = getUserFromSecurityContext();
        Task taskToDelete = taskRepository.findById(UUID.fromString(taskId)).orElseThrow(TaskNotFoundException::new);
        if (taskToDelete.getAuthor().equals(authenticatedUser)) {
            taskRepository.delete(taskToDelete);
        } else throw new PermissionDeniedException();
    }

    @Override
    public List<TaskViewDTO> getTaskByAuthor(String userId, TaskStatus status, PriorityLevel priority, String pageStr, String sizeStr) {
        long page = Long.parseLong(pageStr);
        long size = Long.parseLong(sizeStr);

        if(page < 0 || page > size || size < 1) {
            throw new IllegalArgumentException("Bad pagination parameters");
        }

        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(UserNotFoundException::new);
        List<TaskViewDTO> tasks = taskRepository.findAllByAuthor(user).stream()
                .filter(task -> (status ==  null || task.getStatus().getName().equals(status.getName())))
                .filter(task -> (priority == null || task.getPriority().getName().equals(priority.getName())))
                .skip( page * size)
                .limit(size)
                .map(taskMapper::toTaskDto)
                .toList();
        if(tasks.isEmpty()) throw new TaskNotFoundException("Tasks not found");
        return tasks;
    }

    @Override
    public List<TaskViewDTO> getTaskByAssignee(String userId, TaskStatus status, PriorityLevel priority, String pageStr, String sizeStr) {
        long page = Long.parseLong(pageStr);
        long size = Long.parseLong(sizeStr);

        if(page < 0 || page > size || size < 1) {
            throw new IllegalArgumentException("Bad pagination parameters");
        }

        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(UserNotFoundException::new);
        List<TaskViewDTO> tasks = taskRepository.findAllByAssignee(user).stream()
                .filter(task -> (status ==  null || task.getStatus().getName().equals(status.getName())))
                .filter(task -> (priority == null || task.getPriority().getName().equals(priority.getName())))
                .skip( page * size)
                .limit(size)
                .map(taskMapper::toTaskDto)
                .toList();
        if(tasks.isEmpty()) throw new TaskNotFoundException("Tasks not found");
        return tasks;
    }

    private User getUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.user();
    }

    private void updateMainTaskInfo(Task taskToUpdate, TaskUpdateDTO update) {
        if (update.getTitle() != null) {
            taskToUpdate.setTitle(update.getTitle());
        }
        if (update.getDescription() != null) {
            taskToUpdate.setDescription(update.getDescription());
        }
        if (update.getStatus() != null) {
            taskToUpdate.setStatus(update.getStatus());
        }
        if (update.getAssigneeEmail() != null) {
            taskToUpdate.setPriority(update.getPriority());
        }
        if (update.getAssigneeEmail() != null) {
            User newAssignee = userRepository.findByEmail(update.getAssigneeEmail())
                    .orElseThrow(() -> new UserNotFoundException("Assignee not found"));
            taskToUpdate.setAssignee(newAssignee);
        }
    }
}
