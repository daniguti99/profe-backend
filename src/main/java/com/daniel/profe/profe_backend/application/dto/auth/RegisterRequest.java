package com.daniel.profe.profe_backend.application.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Rellene el nombre de usuario")
    private String username;

    @NotBlank(message = "Rellene el nombre")
    private String firstName;

    @NotBlank(message = "Rellene el/los apellidos")
    private String lastName;

    @Email(message = "Email inválido")
    private String email;

    @Email(message = "Email inválido")
    private String confirmEmail;

    @NotBlank(message = "Rellene la contraseña")
    private String password;

    @NotBlank(message = "Rellene la contraseña de confirmación")
    private String confirmPassword;

}
