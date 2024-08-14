package com.example.taskmanagment.adapters.in.rest.controllers;

import com.example.taskmanagment.adapters.in.security.dto.JwtAuthenticationDto;
import com.example.taskmanagment.adapters.in.security.dto.RefreshTokenDto;
import com.example.taskmanagment.adapters.in.security.dto.UserCredentialsDto;
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
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.stream.Collectors;

@Tag(name = "Authentication Controller", description = "Эндпоинты связанны с аутентификацией пользователя в API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    @Operation(
            summary = "Вход по логину и паролю",
            description = """
                    Получает данные пользователя, проверяет их подлинность и возвращает токен аутентификации и токен обновления.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аутентификация прошла успешно", content = @Content(schema = @Schema(implementation = JwtAuthenticationDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationDto> signIn(@RequestBody UserCredentialsDto userCredentialsDto) throws AuthenticationException {
        JwtAuthenticationDto jwtAuthenticationDto = userService.signIn(userCredentialsDto);
        return ResponseEntity.ok(jwtAuthenticationDto);
    }

    @Operation(
            summary = "Получение токена авторизации через токен обновления",
            description = """
                    Получает токен обновления, проверяет его подлинность и возвращает новый токен аутентификации и текущий токен обновления.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Повторная аутентификация прошла успешно", content = @Content(schema = @Schema(implementation = JwtAuthenticationDto.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Ошибка на стороне сервера", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/refresh")
    public JwtAuthenticationDto refresh(@RequestBody RefreshTokenDto refreshTokenDto) throws Exception {
        return userService.refreshToken(refreshTokenDto);
    }


}
