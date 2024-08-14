package com.example.taskmanagment.adapters.in.rest.controllers;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.example.taskmanagment.adapters.in.rest.exceptions.UserNotFoundException;
import com.example.taskmanagment.adapters.in.security.CustomUserDetails;
import com.example.taskmanagment.adapters.in.security.CustomUserServiceImpl;
import com.example.taskmanagment.adapters.in.security.jwt.JwtServiceImpl;
import com.example.taskmanagment.application.domain.models.User;
import com.example.taskmanagment.application.ports.in.TaskService;
import com.example.taskmanagment.application.ports.out.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.test.web.servlet.MockMvc;


import java.util.Optional;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    private final String FIND_USER_BY_EMAIL_URL = "/user/email/{email}";
    private final String FIND_USER_BY_ID_URL = "/user/{userId}";

    @MockBean
    private CustomUserServiceImpl customUserService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtServiceImpl jwtServiceImpl;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @InjectMocks
    private UserController userController;

    @MockBean
    private UserRepository userRepository;

    private User user;
    private UUID userId;

    private String email;
    private String token;


    @BeforeEach
    void setUp() {
        Mockito.when(customUserService.loadUserByUsername(Mockito.anyString()))
                .thenReturn(new CustomUserDetails(User.builder()
                        .email("test@mail.ru")
                        .password(passwordEncoder.encode("test"))
                        .build()));
        token = jwtServiceImpl.generateAuthToken("test@mail.ru").getToken();

        userId = UUID.randomUUID();
        email = "john.doe@example.com";
        user = new User();
        user.setId(userId);
        user.setUsername("John Doe");
        user.setEmail("john.doe@example.com");

    }

    @Test
    void getUserById_WhenUserExistsTest() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get(FIND_USER_BY_ID_URL, userId.toString())
                        .contentType(MediaType.APPLICATION_JSON).header("AUTHORIZATION", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.toString())))
                .andExpect(jsonPath("$.username", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    void getUserById_WhenUserDoesNotExistTest() throws Exception {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get(FIND_USER_BY_ID_URL, userId.toString())
                        .contentType(MediaType.APPLICATION_JSON).header("AUTHORIZATION", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByEmail_WhenUserExistsTest() throws Exception {
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        mockMvc.perform(get(FIND_USER_BY_EMAIL_URL, email)
                        .contentType(MediaType.APPLICATION_JSON).header("AUTHORIZATION", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.toString())))
                .andExpect(jsonPath("$.username", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john.doe@example.com")));
    }

    @Test
    void getUserByEmail_WhenUserNotExistTest() throws Exception {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        mockMvc.perform(get(FIND_USER_BY_EMAIL_URL, userId.toString())
                        .contentType(MediaType.APPLICATION_JSON).header("AUTHORIZATION", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

}