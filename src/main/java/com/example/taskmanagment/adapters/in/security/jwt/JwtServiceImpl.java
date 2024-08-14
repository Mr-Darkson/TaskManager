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
@Slf4j
@Service
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.token.secret}")
    private String jwtSecret;

    @Value("${jwt.refreshtoken.lifetime}")
    private Long jwtRefreshTokenLifetime;

    @Value("${jwt.token.lifetime}")
    private Long jwtTokenLifetime;

    public JwtAuthenticationDto generateAuthToken(String email) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email));
        jwtDto.setRefreshToken(generateRefreshToken(email));
        return jwtDto;
    }

    public JwtAuthenticationDto refreshBaseToken(String email, String refreshToken) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email));
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

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

    private String generateJwtToken(String email) {
        Date expirationTime = Date.from(LocalDateTime.now().plusMinutes(jwtTokenLifetime).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(expirationTime)
                .signWith(getSignKey())
                .compact();
    }

    private String generateRefreshToken(String email) {
        Date expirationTime = Date.from(LocalDateTime.now().plusMinutes(jwtRefreshTokenLifetime).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(expirationTime)
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
