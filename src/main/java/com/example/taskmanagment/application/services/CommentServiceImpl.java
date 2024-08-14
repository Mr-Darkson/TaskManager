package com.example.taskmanagment.application.services;

import com.example.taskmanagment.adapters.in.rest.exceptions.CommentNotFoundException;
import com.example.taskmanagment.adapters.in.rest.exceptions.PermissionDeniedException;
import com.example.taskmanagment.adapters.in.rest.exceptions.TaskNotFoundException;
import com.example.taskmanagment.adapters.in.security.CustomUserDetails;
import com.example.taskmanagment.application.domain.dto.CommentUpdateDTO;
import com.example.taskmanagment.application.domain.dto.CommentViewDTO;
import com.example.taskmanagment.application.domain.models.Comment;
import com.example.taskmanagment.application.domain.models.Task;
import com.example.taskmanagment.application.domain.models.User;
import com.example.taskmanagment.application.ports.in.CommentService;
import com.example.taskmanagment.application.ports.out.CommentRepository;
import com.example.taskmanagment.application.ports.out.TaskRepository;
import com.example.taskmanagment.application.ports.out.UserRepository;
import com.example.taskmanagment.application.utils.mappers.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    @Override
    public void createComment(CommentUpdateDTO commentDto, String taskId)  {
        User authenticatedUser = getUserFromSecurityContext();

        Comment comment = commentMapper.toComment(commentDto);
        comment.setAuthor(authenticatedUser);
        comment.setTask(taskRepository.findById(UUID.fromString(taskId))
                .orElseThrow(TaskNotFoundException::new));
        comment.setCreatedDate(LocalDateTime.now());

        commentRepository.save(comment);
    }

    @Override
    public void delete(String commentId) throws CommentNotFoundException, PermissionDeniedException {
        User authenticatedUser = getUserFromSecurityContext();
        Comment comment = commentRepository.findById(UUID.fromString(commentId))
                .orElseThrow(CommentNotFoundException::new);
        if(comment.getAuthor().equals(authenticatedUser) || comment.getTask().getAuthor().equals(authenticatedUser)) {
            commentRepository.delete(comment);
        } else throw new PermissionDeniedException();
    }

    @Override
    public List<CommentViewDTO> getCommentsByTask(String taskId, String pageStr, String sizeStr) throws TaskNotFoundException {
        long page = Long.parseLong(pageStr);
        long size = Long.parseLong(sizeStr);

        if(page < 0 || page > size || size < 1) {
            throw new IllegalArgumentException("Bad pagination parameters");
        }

        Task task = taskRepository.findById(UUID.fromString(taskId)).orElseThrow(TaskNotFoundException::new);
        List<CommentViewDTO> comments = commentRepository.findAllCommentsByTask(task).stream()
                .skip( page * size)
                .limit(size)
                .map(commentMapper::commentViewDTO)
                .toList();
        if(comments.isEmpty()) throw new CommentNotFoundException("Comments not found");
        return comments;
    }

    private User getUserFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.user();
    }
}
