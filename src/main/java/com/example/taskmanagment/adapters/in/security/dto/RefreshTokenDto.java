package com.example.taskmanagment.adapters.in.security.dto;

import lombok.Data;


/**
 * DTO хранящее в себе токен обновления (refresh)
 */
@Data
public class RefreshTokenDto {
    private String refreshToken;
}
