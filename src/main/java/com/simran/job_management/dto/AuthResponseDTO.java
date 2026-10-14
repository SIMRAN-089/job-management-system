package com.simran.job_management.dto;

import com.simran.job_management.entity.UserRole;

public class AuthResponseDTO {

    private final String token;
    private final String tokenType;
    private final Long userId;
    private final String name;
    private final String email;
    private final UserRole role;

    public AuthResponseDTO(
            String token,
            String tokenType,
            Long userId,
            String name,
            String email,
            UserRole role) {
        this.token = token;
        this.tokenType = tokenType;
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public String getToken() { return token; }
    public String getTokenType() { return tokenType; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserRole getRole() { return role; }

    public String token() { return token; }
    public String tokenType() { return tokenType; }
    public Long userId() { return userId; }
    public String name() { return name; }
    public String email() { return email; }
    public UserRole role() { return role; }
}