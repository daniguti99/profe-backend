package com.daniel.profe.profe_backend.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Rellene la contraseña")
    private String password;
}
