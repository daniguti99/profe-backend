package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.auth.RegisterRequest;
import com.daniel.profe.profe_backend.application.dto.auth.RegisterResponse;

//Contrato que define el caso de uso de registro de usuario
public interface RegisterUseCase {
    RegisterResponse register(RegisterRequest registerRequest);
}
