package com.example.taskmanagment.adapters.in.rest.controllers;

import com.example.taskmanagment.adapters.in.rest.exceptions.UserNotFoundException;
import com.example.taskmanagment.adapters.in.security.dto.UserCredentialsDto;
import com.example.taskmanagment.application.domain.dto.UserDto;
import com.example.taskmanagment.application.ports.in.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationException;

@Tag(name = "User Controller", description = "Эндпоинты связанны с сущностью 'User'")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "Зарегистрировать нового пользователя",
            description = """
                    Получает логин и пароль для нового пользователя. Если логин (email) ещё не занят, то записывает пользователя в базу данных.
                    Возвращает статус выполнения задачи.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Пользователь создан", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))
            }
    )
    @PostMapping("/registration")
    public ResponseEntity<String> createUser(@Valid @RequestBody UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        userService.createUser(userCredentialsDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User created");
    }


    @Operation(
            summary = "Получить пользователя по email",
            description = """
                    Получает email пользователя. Если он существует в базе данных, то возвращает DTO публичных данных этого пользователя.
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "email", description = "Email пользователя", required = true, schema = @Schema(implementation = String.class))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ответ получен ", content = @Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Ошибка аутентификации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Не авторизован", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))}

    )
    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String email) throws UserNotFoundException {
        UserDto userDto = userService.getUserByEmail(email);
        return ResponseEntity.status(HttpStatus.OK).body(userDto);

    }


    @Operation(
            summary = "Получить пользователя по ID (UUID)",
            description = """
                    Получает ID пользователя. Если он существует в базе данных, то возвращает DTO публичных данных этого пользователя.
                   
                    """,
            security = @SecurityRequirement(name = "bearerAuth"),
            parameters = {
                    @Parameter(name = "userId", description = "UUID пользователя", required = true, schema = @Schema(implementation = String.class))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ответ получен ", content = @Content(schema = @Schema(implementation = UserDto.class), mediaType = "application/json")),
                    @ApiResponse(responseCode = "401", description = "Ошибка аутентификации", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "403", description = "Не авторизован", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content(schema = @Schema(implementation = String.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))
            }

    )
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("userId") String userId) throws UserNotFoundException {
        UserDto userDto = userService.getUserById(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userDto);
    }

}
