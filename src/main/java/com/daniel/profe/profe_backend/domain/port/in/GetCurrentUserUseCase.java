package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.auth.UserInfo;

//Contrato que define el caso de uso para obtener el usuario actual
public interface GetCurrentUserUseCase {
    UserInfo getCurrentUser(String token);
}
