package com.example.taskmanagment.adapters.in.security.jwt;

import com.example.taskmanagment.adapters.in.security.CustomUserDetails;
import com.example.taskmanagment.adapters.in.security.CustomUserServiceImpl;
import com.example.taskmanagment.application.ports.in.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * Фильтр, который ищет в каждом запросе данные для аутентификации
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserServiceImpl customUserService;

    /**
     * @param request Запрос пользователя
     * @param response Ответ пользователю
     * @param filterChain Цепочка фильтров
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        if(token != null && jwtService.validateJwtToken(token)) {
            setCustomUserDetailsToSecurityContextHolder(token);
        }
        filterChain.doFilter(request, response);
    }


    /**
     * Помещает аутентифицированного пользователя в SecurityContextHolder
     * @param token Токен аутентификации
     */
    private void setCustomUserDetailsToSecurityContextHolder(String token) {
        String email = jwtService.getEmailFromToken(token);
        CustomUserDetails customUserDetails = customUserService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken( customUserDetails,
                null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    /**
     * @param request Запрос пользователя
     * @return String - токен аутентификации
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
