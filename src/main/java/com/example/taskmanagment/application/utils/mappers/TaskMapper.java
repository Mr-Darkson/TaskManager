package com.example.taskmanagment.application.utils.mappers;

import com.example.taskmanagment.application.domain.dto.TaskUpdateDTO;
import com.example.taskmanagment.application.domain.dto.TaskViewDTO;
import com.example.taskmanagment.application.domain.models.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@RequiredArgsConstructor
@Component
public class TaskMapper {
    CommentMapper commentMapper = new CommentMapper();


    public Task toTask(TaskUpdateDTO taskDto) {
        Task newTask = new Task();
        newTask.setTitle(taskDto.getTitle());
        newTask.setDescription(taskDto.getDescription());
        newTask.setStatus(taskDto.getStatus());
        newTask.setPriority(taskDto.getPriority());
        newTask.setAssignee(null);
        newTask.setComments(new ArrayList<>());
        return newTask;
    }


    public TaskViewDTO toTaskDto(Task task) {
        TaskViewDTO taskViewDTO = new TaskViewDTO();
        taskViewDTO.setId(task.getId());
        taskViewDTO.setTitle(task.getTitle());
        taskViewDTO.setDescription(task.getDescription());
        taskViewDTO.setStatus(task.getStatus());
        taskViewDTO.setPriority(task.getPriority());
        taskViewDTO.setAuthorEmail(task.getAuthor().getEmail());
        taskViewDTO.setAssigneeEmail(task.getAssignee().getEmail());
        return taskViewDTO;
    }

}
