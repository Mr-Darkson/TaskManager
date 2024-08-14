package com.example.taskmanagment.application.ports.in;

import com.example.taskmanagment.application.domain.dto.CommentUpdateDTO;
import com.example.taskmanagment.application.domain.dto.CommentViewDTO;

import java.util.List;

public interface CommentService {


    void createComment(CommentUpdateDTO commentDto, String taskId);

    void delete(String commentId);

    List<CommentViewDTO> getCommentsByTask(String taskId, String page, String size);
}
