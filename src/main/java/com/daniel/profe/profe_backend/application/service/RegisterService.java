package com.daniel.profe.profe_backend.application.service;

import com.daniel.profe.profe_backend.application.dto.auth.RegisterRequest;
import com.daniel.profe.profe_backend.application.dto.auth.RegisterResponse;
import com.daniel.profe.profe_backend.application.dto.error.ErrorResponse;
import com.daniel.profe.profe_backend.domain.exception.EmailAlreadyExistsException;
import com.daniel.profe.profe_backend.domain.exception.EmailMismatchException;
import com.daniel.profe.profe_backend.domain.exception.UsernameAlreadyExistsException;
import com.daniel.profe.profe_backend.domain.exception.ValidationException;
import com.daniel.profe.profe_backend.domain.model.User;
import com.daniel.profe.profe_backend.domain.model.UserRole;
import com.daniel.profe.profe_backend.domain.port.in.RegisterUseCase;
import com.daniel.profe.profe_backend.domain.port.out.PasswordEncoderPort;
import com.daniel.profe.profe_backend.domain.port.out.UserRepositoryPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class RegisterService implements RegisterUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {

        // Validar el nombre de usuario según las reglas definidas
        validateUsername(registerRequest);

        // Validar si el nombre de usuario ya existe en la base de datos
        existsByUsername(registerRequest);

        // Validar si el email o el nombre de usuario ya existen en la base de datos
        existsByEmail(registerRequest);

        // Validar si los emails coinciden
        validateEmails(registerRequest);

        // Validar si las contraseñas coinciden
        validatePasswords(registerRequest);

        // Construir el objeto User a partir del RegisterRequest
        User user = buildUser(registerRequest);

        // Guardar el usuario en la base de datos
        User savedUser = userRepositoryPort.save(user);

        // Construir la respuesta de registro a partir del usuario guardado
        return buildResponse(savedUser);
    }

    private void existsByEmail(RegisterRequest registerRequest) {
        if(userRepositoryPort.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("El email introducido ya existe");
        }
    }

    private void existsByUsername(RegisterRequest registerRequest) {
        if(userRepositoryPort.existsByUsername(registerRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("El nombre de usuario introducido ya existe");
        }
    }

    private void validateEmails(RegisterRequest registerRequest) {
        if(!registerRequest.getEmail().equals(registerRequest.getConfirmEmail())) {
            throw new EmailMismatchException("Los emails introducidos no coinciden");
        }
    }

    private void validatePasswords(RegisterRequest registerRequest) {
        if(!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new EmailMismatchException("Las contraseñas introducidas no coinciden");
        }

        if (registerRequest.getPassword().length() < 8) {
            throw new ValidationException(
                    "La contraseña debe tener al menos 8 caracteres"
            );
        }

        if (!registerRequest.getPassword()
                .matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
            throw new ValidationException(
                    "La contraseña debe contener al menos una letra mayúscula, una letra minúscula y un número"
            );
        }
    }

    private User buildUser(RegisterRequest registerRequest) {
        return User.builder()
                .username(registerRequest.getUsername())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoderPort.encode(registerRequest.getPassword()))
                .role(UserRole.ROLE_USER)
                .province(registerRequest.getProvince())
                .locality(registerRequest.getLocality())
                .build();
    }

    private RegisterResponse buildResponse(User user) {
        return RegisterResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Usuario registrado correctamente")
                .build();
    }

    private void validateUsername(RegisterRequest request) {

        if (request.getUsername().length() < 4) {
            throw new ValidationException(
                    "El nombre de usuario debe tener al menos 4 caracteres"
            );
        }

        if (request.getUsername().length() > 20) {
            throw new ValidationException(
                    "El nombre de usuario no puede tener más de 20 caracteres"
            );
        }

        if (!request.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            throw new ValidationException(
                    "El nombre de usuario solo puede contener letras, números y guiones bajos"
            );
        }
    }


}
