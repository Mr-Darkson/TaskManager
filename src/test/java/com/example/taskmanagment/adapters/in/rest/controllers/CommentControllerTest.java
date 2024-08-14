package com.example.taskmanagment.adapters.in.rest.controllers;

import com.example.taskmanagment.adapters.in.security.CustomUserDetails;
import com.example.taskmanagment.adapters.in.security.CustomUserServiceImpl;
import com.example.taskmanagment.adapters.in.security.jwt.JwtServiceImpl;
import com.example.taskmanagment.application.domain.dto.CommentUpdateDTO;
import com.example.taskmanagment.application.domain.dto.CommentViewDTO;
import com.example.taskmanagment.application.domain.dto.TaskUpdateDTO;
import com.example.taskmanagment.application.domain.enums.PriorityLevel;
import com.example.taskmanagment.application.domain.enums.TaskStatus;
import com.example.taskmanagment.application.domain.models.Comment;
import com.example.taskmanagment.application.domain.models.Task;
import com.example.taskmanagment.application.domain.models.User;
import com.example.taskmanagment.application.ports.out.CommentRepository;
import com.example.taskmanagment.application.ports.out.TaskRepository;
import com.example.taskmanagment.application.ports.out.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CommentControllerTest {
    private final String CREATE_COMMENT_URL = "/comment/{taskId}";
    private final String DELETE_COMMENT_URL = "/comment/{commentId}";

    private final String GET_COMMENTS_BY_TASK_URL = "/comment/{taskId}";
    private final String GET_COMMENTS_BY_TASK_WITH_PARAMETERS_URL = "/comment/{taskId}?page={page}&size={size}";

    @MockBean
    private CustomUserServiceImpl customUserService;
    @Autowired
    private JwtServiceImpl jwtServiceImpl;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @InjectMocks
    private TaskController commentController;

    @MockBean
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    private Task validTask;

    private Comment ownedComment;
    private Comment notOwnedComment;

    private List<Comment> listOfComments;

    private CommentUpdateDTO validCommentUpdateDTO;
    private CommentUpdateDTO invalidCommentUpdateDTO;

    private UUID userId;
    private UUID taskId;
    private UUID commentId;
    private String email;
    private String token;


    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        userId = UUID.randomUUID();
        commentId = UUID.randomUUID();
        email = "test@mail.ru";

        user = User.builder()
                .id(userId)
                .username("John Doe")
                .email(email)
                .build();
        validTask = Task.builder()
                .id(taskId)
                .title("Test title")
                .description("Test description")
                .status(TaskStatus.WAITING)
                .priority(PriorityLevel.LOW)
                .author(user)
                .assignee(user)
                .build();

        ownedComment = Comment.builder()
                .id(commentId)
                .content("Test content")
                .createdDate(LocalDateTime.now())
                .author(user)
                .task(validTask)
                .build();

        notOwnedComment = Comment.builder()
                .id(commentId)
                .content("Test content")
                .createdDate(LocalDateTime.now())
                .author(new User())
                .task(Task.builder().author(new User()).build())
                .build();

        listOfComments = List.of(
                Comment.builder()
                        .id(UUID.randomUUID())
                        .content("Test content1")
                        .author(user)
                        .task(validTask)
                        .createdDate(LocalDateTime.now())
                        .build()
                ,
                Comment.builder()
                        .id(UUID.randomUUID())
                        .content("Test content2")
                        .author(new User())
                        .task(validTask)
                        .createdDate(LocalDateTime.now())
                        .build()
                ,
                Comment.builder()
                        .id(UUID.randomUUID())
                        .content("Test content3")
                        .author(new User())
                        .task(new Task())
                        .createdDate(LocalDateTime.now())
                        .build());

        validCommentUpdateDTO = new CommentUpdateDTO();
        validCommentUpdateDTO.setContent("Test comment");

        invalidCommentUpdateDTO = new CommentUpdateDTO();
        invalidCommentUpdateDTO.setContent("");

        Mockito.when(customUserService.loadUserByUsername(Mockito.anyString()))
                .thenReturn(new CustomUserDetails(user));
        token = jwtServiceImpl.generateAuthToken("test@mail.ru").getToken();
    }

    @Test
    void createComment_WithValidDataTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(validTask));
        doNothing().when(commentRepository).save(any(Comment.class));
        mockMvc.perform(post(CREATE_COMMENT_URL, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AUTHORIZATION", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(validCommentUpdateDTO))
                )
                .andExpect(status().isCreated());
    }

    @Test
    void createComment_WithInvalidDataTest() throws Exception {
        mockMvc.perform(post(CREATE_COMMENT_URL, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AUTHORIZATION", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(invalidCommentUpdateDTO))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void createComment_WhenTaskNotExistsTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        mockMvc.perform(post(CREATE_COMMENT_URL, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AUTHORIZATION", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(validCommentUpdateDTO))
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void deleteComment_WhenCommentExistsTest() throws Exception {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(ownedComment));
        mockMvc.perform(delete(DELETE_COMMENT_URL, commentId)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteComment_WhenCommentNotExistsTest() throws Exception {
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        mockMvc.perform(delete(DELETE_COMMENT_URL, commentId)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteComment_WhenNotHasPermissionsTest() throws Exception {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(notOwnedComment));
        mockMvc.perform(delete(DELETE_COMMENT_URL, commentId)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void getCommentsByTask_WithoutParamsTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(validTask));
        when(commentRepository.findAllCommentsByTask(validTask)).thenReturn(listOfComments);
        mockMvc.perform(get(GET_COMMENTS_BY_TASK_URL, taskId)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    void getCommentsByTask_WithParamsDataTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(validTask));
        when(commentRepository.findAllCommentsByTask(validTask)).thenReturn(listOfComments);
        mockMvc.perform(get(GET_COMMENTS_BY_TASK_WITH_PARAMETERS_URL, taskId, "0", "5")
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }

    @Test
    void getCommentsByTask_WithBadParamsDataTest() throws Exception {
        mockMvc.perform(get(GET_COMMENTS_BY_TASK_WITH_PARAMETERS_URL, taskId, "-1", "0")
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCommentsByTask_WithoutTaskTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        mockMvc.perform(get(GET_COMMENTS_BY_TASK_URL, taskId)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void getCommentsByTask_WhenCommentsNotExists() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(validTask));
        when(commentRepository.findAllCommentsByTask(validTask)).thenReturn(Collections.emptyList());
        mockMvc.perform(get(GET_COMMENTS_BY_TASK_URL, taskId)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isNotFound());
    }


}