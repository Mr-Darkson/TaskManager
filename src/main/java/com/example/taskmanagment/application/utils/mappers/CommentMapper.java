package com.example.taskmanagment.application.utils.mappers;

import com.example.taskmanagment.application.domain.dto.CommentUpdateDTO;
import com.example.taskmanagment.application.domain.dto.CommentViewDTO;
import com.example.taskmanagment.application.domain.models.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentMapper {

    public Comment toComment(CommentUpdateDTO commentUpdateDTO) {
        Comment comment = new Comment();
        comment.setContent(comment.getContent());
        comment.setTask(null);
        return comment;
    }

    public CommentViewDTO commentViewDTO(Comment comment) {
        CommentViewDTO commentViewDTO = new CommentViewDTO();
        commentViewDTO.setId(comment.getId());
        commentViewDTO.setContent(comment.getContent());
        commentViewDTO.setCreatedDate(comment.getCreatedDate());
        commentViewDTO.setAuthor(comment.getAuthor().getEmail());
        commentViewDTO.setTask(comment.getTask().getId());
        return commentViewDTO;
    }
}
