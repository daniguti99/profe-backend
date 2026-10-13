package com.daniel.profe.profe_backend.domain.port.in;

import com.daniel.profe.profe_backend.application.dto.session.CreateSessionRequest;
import com.daniel.profe.profe_backend.application.dto.session.CreateSessionResponse;

public interface CreateSessionUseCase {
    CreateSessionResponse createSession(CreateSessionRequest request, Long userId);
}
