package com.daniel.profe.profe_backend.application.dto.auth;


import com.daniel.profe.profe_backend.domain.model.UserRole;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
}
