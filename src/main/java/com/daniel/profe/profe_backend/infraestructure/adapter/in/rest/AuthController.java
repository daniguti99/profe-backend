package com.daniel.profe.profe_backend.infraestructure.adapter.in.rest;

import com.daniel.profe.profe_backend.application.dto.auth.LoginRequest;
import com.daniel.profe.profe_backend.application.dto.auth.LoginResponse;
import com.daniel.profe.profe_backend.application.dto.auth.RegisterRequest;
import com.daniel.profe.profe_backend.application.dto.auth.RegisterResponse;
import com.daniel.profe.profe_backend.application.dto.auth.UserInfo;
import com.daniel.profe.profe_backend.domain.port.in.GetCurrentUserUseCase;
import com.daniel.profe.profe_backend.domain.port.in.LoginUseCase;
import com.daniel.profe.profe_backend.domain.port.in.RegisterUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Tag(name = "Autenticación", description = "Registro, inicio de sesión y usuario actual.")
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final RegisterUseCase registerUseCase;
    private final LoginUseCase loginUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;

    @Operation(summary = "Registra un nuevo usuario", description = "Devuelve 201 y el usuario creado. No requiere token.")
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        RegisterResponse registerResponse = registerUseCase.register(registerRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registerResponse);
    }

    @Operation(summary = "Inicia sesión", description = "Devuelve 201 con el token JWT. No requiere token previo.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = loginUseCase.login(loginRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(loginResponse);
    }

    @Operation(summary = "Devuelve el usuario del token", description = "Requiere el header Authorization con el token Bearer.")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserInfo> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        UserInfo userInfo = getCurrentUserUseCase.getCurrentUser(token);
        return ResponseEntity.ok(userInfo);
    }
}
