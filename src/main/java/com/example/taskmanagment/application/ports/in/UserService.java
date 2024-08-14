package com.example.taskmanagment.application.ports.in;

import com.example.taskmanagment.adapters.in.rest.exceptions.UserNotFoundException;
import com.example.taskmanagment.adapters.in.security.dto.JwtAuthenticationDto;
import com.example.taskmanagment.adapters.in.security.dto.RefreshTokenDto;
import com.example.taskmanagment.adapters.in.security.dto.UserCredentialsDto;
import com.example.taskmanagment.application.domain.dto.UserDto;
import org.springframework.data.crossstore.ChangeSetPersister;

import javax.naming.AuthenticationException;

public interface UserService {
    JwtAuthenticationDto signIn(UserCredentialsDto userCredentialsDto) throws AuthenticationException;

    JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto);

    void createUser(UserCredentialsDto user) throws AuthenticationException;

    UserDto getUserByEmail(String email) throws UserNotFoundException;

    UserDto getUserById(String id) throws UserNotFoundException;

}
