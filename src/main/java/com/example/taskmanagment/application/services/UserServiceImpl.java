package com.example.taskmanagment.application.services;

import com.example.taskmanagment.adapters.in.rest.exceptions.UserNotFoundException;
import com.example.taskmanagment.adapters.in.security.dto.JwtAuthenticationDto;
import com.example.taskmanagment.adapters.in.security.dto.RefreshTokenDto;
import com.example.taskmanagment.adapters.in.security.dto.UserCredentialsDto;
import com.example.taskmanagment.application.domain.dto.UserDto;
import com.example.taskmanagment.application.domain.models.User;
import com.example.taskmanagment.application.ports.in.JwtService;
import com.example.taskmanagment.application.ports.in.UserService;
import com.example.taskmanagment.application.ports.out.UserRepository;
import com.example.taskmanagment.application.utils.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Класс реализующий бизнес-логику UserService
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    
    /**
     * @param userCredentialsDto DTO хранящее в себе данные для входа
     * @return JwtAuthenticationDto возвращает DTO, хранящее в себе токен аутентификации и обновления
     * @throws AuthenticationServiceException Ошибка аутентификации
     */
    @Override
    public JwtAuthenticationDto signIn(UserCredentialsDto userCredentialsDto) throws AuthenticationServiceException {
        User user = findByCredentials(userCredentialsDto);
        return jwtService.generateAuthToken(user.getEmail());
    }

    /**
     * @param refreshTokenDto Принимает на вход ДТО с токеном обновления и создаёт по нему новый токен доступа
     * @return JwtAuthenticationDto. Хранит в себе токены доступа и обновления
     * @throws UserNotFoundException Выбрасывается, если не может найти пользователя по токену обновления
     */
    @Override
    public JwtAuthenticationDto refreshToken(RefreshTokenDto refreshTokenDto) throws UserNotFoundException {
        String refreshToken = refreshTokenDto.getRefreshToken();
        if(refreshToken != null && jwtService.validateJwtToken(refreshToken)) {
            User user = findByEmail(jwtService.getEmailFromToken(refreshToken));
            return jwtService.refreshBaseToken(user.getEmail(), refreshToken);
        }
        throw new AuthenticationServiceException("Invalid refresh token");
    }


    /**
     * Создаёт пользователя на основе переданных в него данных (Логин и пароль)
     * @param authDto Данные для создания пользователя
     */
    @Override
    public void createUser(UserCredentialsDto authDto) {
        if(userRepository.findByEmail(authDto.getEmail()).isPresent()) {
            throw new AuthenticationServiceException("User already exists");
        }
        User user = userMapper.toUser(authDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedDate(LocalDateTime.now());
        userRepository.save(user);
    }


    /**
     * Ищет пользователя по его email и возвращает в случае удачи.
     * @param email почта пользователя
     * @return UserDto Данные пользователя для отображения
     * @throws UserNotFoundException Выбрасывается, если не найден пользователь
     */
    @Override
    public UserDto getUserByEmail(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(String.format("User with email %s not found", email)));
        return userMapper.toUserDto(user);
    }

    /**
     * Ищет пользователя по его ID и возвращает в случае удачи.
     * @param id идентификатор пользователя
     * @return UserDto Данные пользователя для отображения
     * @throws UserNotFoundException Выбрасывается, если не найден пользователь
     */
    @Override
    public UserDto getUserById(String id)  throws UserNotFoundException {
        User user = userRepository.findById(UUID.fromString(id)).orElseThrow(() -> new UserNotFoundException(String.format("User with ID %s not found", id)));
        return userMapper.toUserDto(user);
    }

    private User findByCredentials(UserCredentialsDto credentialsDto) throws AuthenticationServiceException {
        Optional<User> optionalUser = userRepository.findByEmail(credentialsDto.getEmail());
        if(optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(credentialsDto.getPassword(), user.getPassword())) {
                return user;
            }
        }
        throw new AuthenticationServiceException("Email or password is incorrect");
    }
    private User findByEmail(String email) throws UserNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with email %s not found", email)));
    }
}
