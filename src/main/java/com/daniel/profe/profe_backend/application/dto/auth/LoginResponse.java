package com.daniel.profe.profe_backend.application.dto.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
    private String tokenType;
    private Long expiresInMs;
    private UserInfo user;
    private String message;
}
