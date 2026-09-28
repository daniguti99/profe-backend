package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.auth.LoginRequest;
import com.daniel.profe.profe_backend.application.dto.auth.LoginResponse;

//Contrato que define el caso de uso de login de usuario
public interface LoginUseCase {
    LoginResponse login(LoginRequest loginRequest);
}
