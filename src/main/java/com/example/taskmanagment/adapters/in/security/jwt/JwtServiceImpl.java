package com.example.taskmanagment.adapters.in.security.jwt;


import com.example.taskmanagment.adapters.in.security.dto.JwtAuthenticationDto;
import com.example.taskmanagment.application.ports.in.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Класс отвечает за работу с JWT токенами: генерацию и извлечение данных.
 */
@Slf4j
@Service
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.token.secret}")
    private String jwtSecret;

    /**Время жизни токена обновления в минутах**/
    @Value("${jwt.refreshtoken.lifetime}")
    private Long jwtRefreshTokenLifetime;

    /**Время жизни токена аутентификации в минутах**/
    @Value("${jwt.token.lifetime}")
    private Long jwtTokenLifetime;


    /**
     * Создаёт новые данные для аутентификации на основе ключа пользователя
     * @param email выступает в качестве ключа для пользователя
     * @return JwtAuthenticationDto возвращает готовый токен аутентификации и обновления
     */
    public JwtAuthenticationDto generateAuthToken(String email) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email));
        jwtDto.setRefreshToken(generateRefreshToken(email));
        return jwtDto;
    }

    /**
     * Создаёт новые данные для аутентификации на основе существующего токена обновления
     * @param email выступает в качестве ключа для пользователя
     * @param refreshToken токен обновления
     * @return JwtAuthenticationDto возвращает готовый токен аутентификации и обновления
     */
    public JwtAuthenticationDto refreshBaseToken(String email, String refreshToken) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email));
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
    }

    /**
     * Извлекает из токена email пользователя
     * @param token Токен аутентификации
     * @return (String) Email пользователя
     */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Проверяет токен аутентификации на валидность
     * @param token Токен аутентификации
     * @return true or false
     */
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException expEx) {
            log.error("Expired JWT token", expEx);
        }
        catch (UnsupportedJwtException expEx) {
            log.error("Unsupported JWT token", expEx);
        }
        catch (MalformedJwtException expEx) {
            log.error("Malformed JWT token", expEx);
        } catch (SecurityException expEx) {
            log.error("Security exception", expEx);
        }
        catch (Exception expEx) {
            log.error("Invalid token", expEx);
        }
        return false;
    }

    /**
     * Генерирует новый JWT токен аутентификации согласно заданным условиям.
     * @param email - ключевые данные пользователя
     * @return (String) JWT токен аутентификации
     */
    private String generateJwtToken(String email) {
        Date expirationTime = Date.from(LocalDateTime.now().plusMinutes(jwtTokenLifetime).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(expirationTime)
                .signWith(getSignKey())
                .compact();
    }

    /**
     * Генерирует новый JWT токен обновления согласно заданным условиям.
     * @param email - ключевые данные пользователя
     * @return (String) JWT токен обновления
     */
    private String generateRefreshToken(String email) {
        Date expirationTime = Date.from(LocalDateTime.now().plusMinutes(jwtRefreshTokenLifetime).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(expirationTime)
                .signWith(getSignKey())
                .compact();
    }


    /**
     * Создаёт секретный ключ для генерации токенов
     * @return SecretKey - секретный ключ
     */
    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
