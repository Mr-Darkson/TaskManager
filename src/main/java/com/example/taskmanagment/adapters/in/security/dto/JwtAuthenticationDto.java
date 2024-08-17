package com.example.taskmanagment.adapters.in.security.dto;

import lombok.Data;

/**
 * DTO хранящее в себе токен аутентификации и токен обновления (refresh)
 */
@Data
public class JwtAuthenticationDto {
    private String token;
    private String refreshToken;
}
