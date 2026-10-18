package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.auth.RegisterRequest;
import com.daniel.profe.profe_backend.application.dto.auth.RegisterResponse;
import com.daniel.profe.profe_backend.domain.model.FotoPerfil;

//Contrato que define el caso de uso de registro de usuario
public interface RegisterUseCase {

    /**
     * Registra un nuevo usuario con su foto de perfil opcional.
     *
     * @param registerRequest datos del formulario de registro
     * @param foto            foto elegida por el usuario, o {@code null} si no ha puesto ninguna
     */
    RegisterResponse register(RegisterRequest registerRequest, FotoPerfil foto);
}
