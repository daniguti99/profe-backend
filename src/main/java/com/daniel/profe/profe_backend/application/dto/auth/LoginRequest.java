package com.daniel.profe.profe_backend.application.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "Rellene el email o nombre de usuario")
    private String login;

    @NotBlank(message = "Rellene la contraseña")
    private String password;
}
