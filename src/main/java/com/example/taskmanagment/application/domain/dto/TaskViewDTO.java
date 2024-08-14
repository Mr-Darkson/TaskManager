package com.example.taskmanagment.application.domain.dto;


import com.example.taskmanagment.application.domain.enums.PriorityLevel;
import com.example.taskmanagment.application.domain.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class TaskViewDTO {

    private UUID id;
    private String title;
    private String description;
    private TaskStatus status;
    private PriorityLevel priority;
    private String authorEmail;
    private String assigneeEmail;

}
