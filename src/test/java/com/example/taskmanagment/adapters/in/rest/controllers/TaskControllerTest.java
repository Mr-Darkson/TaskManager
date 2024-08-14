package com.example.taskmanagment.adapters.in.rest.controllers;

import com.example.taskmanagment.adapters.in.security.CustomUserDetails;
import com.example.taskmanagment.adapters.in.security.CustomUserServiceImpl;
import com.example.taskmanagment.adapters.in.security.jwt.JwtServiceImpl;
import com.example.taskmanagment.application.domain.dto.TaskUpdateDTO;
import com.example.taskmanagment.application.domain.dto.TaskViewDTO;
import com.example.taskmanagment.application.domain.enums.PriorityLevel;
import com.example.taskmanagment.application.domain.enums.TaskStatus;
import com.example.taskmanagment.application.domain.models.Task;
import com.example.taskmanagment.application.domain.models.User;
import com.example.taskmanagment.application.ports.out.TaskRepository;
import com.example.taskmanagment.application.ports.out.UserRepository;
import com.example.taskmanagment.application.utils.mappers.TaskMapper;
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

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    private final String CREATE_TASK_URL = "/tasks";
    private final String MAIN_TASK_URL = "/tasks/{taskId}";
    private final String CHANGE_TASK_STATUS_URL = "/tasks/{taskId}/status?status={status}";
    private final String CHANGE_TASK_ASSIGNEE_URL = "/tasks/{taskId}/assignee?assignee={assignee}";

    private final String GET_TASKS_BY_AUTHOR_WITH_PARAMS_URL = "/tasks/{userID}/created?status={status}&priority={priority}&page={page}&size={size}";
    private final String GET_TASKS_BY_AUTHOR_URL = "/tasks/{userID}/created";
    private final String GET_TASKS_BY_ASSIGNEE_WITH_PARAMS_URL = "/tasks/{userID}/perform?status={status}&priority={priority}&page={page}&size={size}";
    private final String GET_TASKS_BY_ASSIGNEE_URL = "/tasks/{userID}/perform";
    @MockBean
    private CustomUserServiceImpl customUserService;
    @Autowired
    private JwtServiceImpl jwtServiceImpl;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @InjectMocks
    private TaskController taskController;

    @MockBean
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    private TaskUpdateDTO validTaskUpdateDTO;
    private TaskUpdateDTO notValidTaskUpdateDTO;
    private Task validTask;
    private Task notOwnedTask;

    private List<Task> listOfTasks;
    private UUID userId;
    private UUID taskId;
    private String email;
    private String newEmail;
    private String token;


    @MockBean
    private TaskRepository taskRepository;



    @BeforeEach
    void setUp() {

        taskId = UUID.randomUUID();
        userId = UUID.randomUUID();
        email = "test@mail.ru";
        newEmail = "forTest2@mail.ru";

        user = User.builder()
                .id(userId)
                .username("John Doe")
                .email(email)
                .build();

        validTaskUpdateDTO = TaskUpdateDTO.builder()
                .title("test title2")
                .description("test description2")
                .status(TaskStatus.WAITING)
                .priority(PriorityLevel.LOW)
                .assigneeEmail(user.getEmail())
                .build();


        notValidTaskUpdateDTO = TaskUpdateDTO.builder()
                .title(null)
                .description("test description")
                .status(null)
                .priority(null)
                .assigneeEmail(user.getEmail())
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

        notOwnedTask = Task.builder()
                .id(taskId)
                .title("Test title")
                .description("Test description")
                .status(TaskStatus.WAITING)
                .priority(PriorityLevel.LOW)
                .author(new User())
                .assignee(new User())
                .build();


        listOfTasks = List.of(
                Task.builder()
                        .title("Test waiting")
                        .author(user)
                        .assignee(user)
                        .status(TaskStatus.WAITING)
                        .priority(PriorityLevel.LOW)
                .build(),
                Task.builder()
                        .title("Test waiting")
                        .author(user)
                        .assignee(user)
                        .status(TaskStatus.IN_PROGRESS)
                        .priority(PriorityLevel.MEDIUM)
                .build(),
                Task.builder()
                        .title("Test waiting")
                        .author(user)
                        .assignee(user)
                        .status(TaskStatus.COMPLETED)
                        .priority(PriorityLevel.HIGH)
                .build()
        );

        Mockito.when(customUserService.loadUserByUsername(Mockito.anyString()))
                .thenReturn(new CustomUserDetails(user));
        token = jwtServiceImpl.generateAuthToken("test@mail.ru").getToken();

    }

    @Test
    void createdTask_WhenDataValidTest() throws Exception {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        doNothing().when(taskRepository).save(any(Task.class));
        mockMvc.perform(post(CREATE_TASK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AUTHORIZATION", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(validTaskUpdateDTO))
                )
                .andExpect(status().isCreated());
    }
    @Test
    void createdTask_WhenDataNotValidTest() throws Exception {
        mockMvc.perform(post(CREATE_TASK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AUTHORIZATION", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(notValidTaskUpdateDTO))
                )
                .andExpect(status().isBadRequest());
    }
    @Test
    void createdTask_WhenOnlyAssigneeNotValidTest() throws Exception {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        mockMvc.perform(post(CREATE_TASK_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AUTHORIZATION", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(validTaskUpdateDTO))
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void getTask_WhenTaskExistsTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(validTask));
        mockMvc.perform(get(MAIN_TASK_URL, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isOk());
    }
    @Test
    void getTask_WhenTaskNotExistsTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        mockMvc.perform(get(MAIN_TASK_URL, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void updateTask_WhenTaskExistsAndDataValidTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(validTask));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        doNothing().when(taskRepository).save(any(Task.class));

        mockMvc.perform(patch(MAIN_TASK_URL, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AUTHORIZATION", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(validTaskUpdateDTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(validTaskUpdateDTO.getTitle()))
                .andExpect(jsonPath("$.description").value(validTaskUpdateDTO.getDescription()))
                .andExpect(jsonPath("$.status").value(validTaskUpdateDTO.getStatus().getName()))
                .andExpect(jsonPath("$.priority").value(validTaskUpdateDTO.getPriority().getName()))
                .andExpect(jsonPath("$.assigneeEmail").value(validTaskUpdateDTO.getAssigneeEmail()));


    }
    @Test
    void updateTask_WhenTaskExistsAndDataNotValidTest() throws Exception {
        mockMvc.perform(patch(MAIN_TASK_URL, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AUTHORIZATION", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(notValidTaskUpdateDTO))
                )
                .andExpect(status().isBadRequest());
    }
    @Test
    void updateTask_WhenTaskExistsAndAssigneeNotExistsTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(validTask));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        mockMvc.perform(patch(MAIN_TASK_URL, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AUTHORIZATION", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(validTaskUpdateDTO))
                )
                .andExpect(status().isNotFound());

    }
    @Test
    void updateTask_WhenTaskNotExistsTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        mockMvc.perform(patch(MAIN_TASK_URL, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AUTHORIZATION", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(validTaskUpdateDTO))
                )
                .andExpect(status().isNotFound());

    }
    @Test
    void updateTask_WhenUserNotHavePermissionTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(notOwnedTask));

        mockMvc.perform(patch(MAIN_TASK_URL, taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("AUTHORIZATION", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(validTaskUpdateDTO))
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void deleteTask_WhenTaskExistsTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(validTask));
        mockMvc.perform(delete(MAIN_TASK_URL, taskId)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isNoContent());
    }
    @Test
    void deleteTask_WhenTaskNotExistsTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        mockMvc.perform(delete(MAIN_TASK_URL, taskId)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isNotFound());
    }
    @Test
    void deleteTask_WhenUserNotHavePermissionTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(notOwnedTask));
        mockMvc.perform(delete(MAIN_TASK_URL, taskId)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void changeStatus_WhenTaskExistsTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(validTask));
        doNothing().when(taskRepository).save(any(Task.class));
        mockMvc.perform(patch(CHANGE_TASK_STATUS_URL, taskId, "IN_PROGRESS")
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("status").value(TaskStatus.IN_PROGRESS.getName()));
    }
    @Test
    void changeStatus_WhenTaskNotExistsTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        doNothing().when(taskRepository).save(any(Task.class));
        mockMvc.perform(patch(CHANGE_TASK_STATUS_URL, taskId, TaskStatus.COMPLETED)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isNotFound());
    }
    @Test
    void changeStatus_WhenTaskExistsAndUserNotHasPermissionsTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(notOwnedTask));
        doNothing().when(taskRepository).save(any(Task.class));
        mockMvc.perform(patch(CHANGE_TASK_STATUS_URL, taskId, TaskStatus.COMPLETED)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void changeAssignee_WhenTaskExistsTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(validTask));
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.of(user));
        doNothing().when(taskRepository).save(any(Task.class));
        mockMvc.perform(patch(CHANGE_TASK_ASSIGNEE_URL, taskId, newEmail)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("assigneeEmail").value(user.getEmail()));
    }
    @Test
    void changeAssignee_WhenTaskNotExistsTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
        mockMvc.perform(patch(CHANGE_TASK_ASSIGNEE_URL, taskId, newEmail)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isNotFound());
    }
    @Test
    void changeAssignee_WhenTaskExistsButUserForAssigneeNotExistsTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(validTask));
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
        mockMvc.perform(patch(CHANGE_TASK_ASSIGNEE_URL, taskId, newEmail)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isNotFound());
    }
    @Test
    void changeAssignee_WhenTaskExistsButUserNotHasPermissionsTest() throws Exception {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(notOwnedTask));
        mockMvc.perform(patch(CHANGE_TASK_ASSIGNEE_URL, taskId, newEmail)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isForbidden());
    }


    @Test
    void getTasksByAuthor_WithEmptyParamsTest() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findAllByAuthor(user)).thenReturn(listOfTasks);
        mockMvc.perform(get(GET_TASKS_BY_AUTHOR_URL, userId)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }
    @Test
    void getTasksByAuthor_WithFiltersAndPaginationTest() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findAllByAuthor(user)).thenReturn(listOfTasks);
        mockMvc.perform(get(GET_TASKS_BY_AUTHOR_WITH_PARAMS_URL, userId, "COMPLETED", "HIGH", "0", "1")
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }
    @Test
    void getTasksByAuthor_WithBadFiltersAndPaginationTest() throws Exception {
        mockMvc.perform(get(GET_TASKS_BY_AUTHOR_WITH_PARAMS_URL, userId, "Compsleted", "HIGaH", 0, 1)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isBadRequest());
    }
    @Test
    void getTasksByAuthor_WithFiltersAndBadPaginationTest() throws Exception {
        mockMvc.perform(get(GET_TASKS_BY_AUTHOR_WITH_PARAMS_URL, userId, "COMPLETED", "HIGH", -1, 0)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isBadRequest());
    }
    @Test
    void getTasksByAuthor_WhenAuthorNotExistTest() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        mockMvc.perform(get(GET_TASKS_BY_AUTHOR_URL, userId)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isNotFound());
    }
    @Test
    void getTasksByAuthor_WhenTasksNotExistsTest() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findAllByAuthor(user)).thenReturn(Collections.emptyList());
        mockMvc.perform(get(GET_TASKS_BY_AUTHOR_URL, userId)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isNotFound());
    }


    @Test
    void getTasksByAssignee_WithoutParamsTest() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findAllByAssignee(user)).thenReturn(listOfTasks);
        mockMvc.perform(get(GET_TASKS_BY_ASSIGNEE_URL, userId)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }
    @Test
    void getTasksByAssignee_WithParamsTest() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findAllByAssignee(user)).thenReturn(listOfTasks);
        mockMvc.perform(get(GET_TASKS_BY_ASSIGNEE_WITH_PARAMS_URL, userId, "COMPLETED", "HIGH", "0", "1")
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
    }
    @Test
    void getTasksByAssignee_WithBadFiltersAndPaginationTest() throws Exception {
        mockMvc.perform(get(GET_TASKS_BY_ASSIGNEE_WITH_PARAMS_URL, userId, "Compsleted", "HIGaH", 0, 1)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isBadRequest());
    }
    @Test
    void getTasksByAssignee_WithFiltersAndBadPaginationTest() throws Exception {
        mockMvc.perform(get(GET_TASKS_BY_ASSIGNEE_WITH_PARAMS_URL, userId, "COMPLETED", "HIGH", -1, 0)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTasksByAssignee_WhenAssigneeNotExistTest() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        mockMvc.perform(get(GET_TASKS_BY_ASSIGNEE_URL, userId)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isNotFound());
    }
    @Test
    void getTasksByAssignee_WhenTasksNotExistTest() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.findAllByAssignee(user)).thenReturn(Collections.emptyList());
        mockMvc.perform(get(GET_TASKS_BY_ASSIGNEE_URL, userId)
                        .header("AUTHORIZATION", "Bearer " + token)
                )
                .andExpect(status().isNotFound());
    }


}