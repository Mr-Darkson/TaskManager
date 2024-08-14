package com.example.taskmanagment.application.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
public class CommentUpdateDTO {
    @NotBlank(message = "Content is mandatory")
    @Size(min = 1, max = 2048)
    private String content;
}
