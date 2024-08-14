package com.example.taskmanagment.application.domain.dto;

import com.example.taskmanagment.application.domain.enums.PriorityLevel;
import com.example.taskmanagment.application.domain.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskUpdateDTO {
    @NotBlank(message = "Title is mandatory")
    @Size(min = 2, max = 64)
    private String title;
    @Size(min = 0, max = 2048)
    private String description;
    @NotNull(message = "Task status is mandatory")
    private TaskStatus status;
    @NotNull(message = "Priority level is mandatory")
    private PriorityLevel priority;
    private String assigneeEmail;
}
