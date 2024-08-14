package com.example.taskmanagment.application.ports.in;

import com.example.taskmanagment.adapters.in.security.dto.JwtAuthenticationDto;

public interface JwtService {
    public JwtAuthenticationDto generateAuthToken(String email);
    public JwtAuthenticationDto refreshBaseToken(String email, String refreshToken);
    public String getEmailFromToken(String token);

    public boolean validateJwtToken(String token);

}
